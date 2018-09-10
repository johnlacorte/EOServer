//This might take an argument to decide to start with an empty world or
//load files maybe one for whether it is building mode
//Commands: newroom, roomname, roomdescription, chat, newpuppet, puppetname, say, look, quit, shutdown
//To add: clone, newexit, roomnumber, puppetnumber, emote
//Need to add checks for empty strings and negative numbers cause thats what the command functions are going to get
//I need a removePlayer that works from a list because I might want to save them before shutting down.
/**
  * The World class constructs a [RoomList], a [PuppetList], and a [MsgQueue] called mudOutput to hold messages until they are
  * sent out on a network stream. The World class has a function for each command and a couple helper functions to help the
  * command functions and the command parser.
  *
  * @param MAX_CONNECTIONS The maximum number of connections used only to check the number in [Msg] objects and listener for new players.
  * @param log The [Logger] object for error messages.
  * @property roomList A [RoomList] of all Rooms in the MUD world.
  * @property puppetList A [PuppetList] of all Puppets in the MUD world.
  * @property defaultHome This constant is the number of the room players start in and their default home value.
  * @property directionArray This is an array of strings for common exit names in an order where the opposite directions is rotateValue positions away.
  * @property rotateValue The difference between a direction and its opposite's index in directionArray. Currently 6. 1=north 7=south 2=northeast 8=southwest
  * @property shortDirectionArray This is an Array the same size as directionArray with the abbreviations for each in the same indexes.
 */
class World(val MAX_CONNECTIONS: Int, var log: Logger){
    var roomList: RoomList
    var puppetList: PuppetList
    var mudOutput: MsgQueue
    val defaultHome = 0
    val directionArray = arrayOf("in", "north", "northeast", "northwest", "up", "west", "out", "south", "southwest", "southeast", "down", "east")
    val shortDirectionArray = arrayOf("", "n", "ne", "nw", "u", "w", "", "s", "sw", "se", "d", "e")
    val rotateValue = 6

    init{
        roomList = RoomList(log)
        puppetList = PuppetList(log)
        mudOutput = MsgQueue(log)
    }

    /**
     * 
     *
     * Looks up a puppet's listener (connection number) and if it is not -1 or a number greater than MAX_CONNECTIONS, 
     * constructs a [Msg] with connection number and message parameter and adds it to the mudOutput MsgQueue. This will
     * do nothing and return true if the listener is -1 because it assumes it is not a player.
     *
     * @param puppetNumber Number of the [Puppet]
     * @param message The message to send out on a network stream if it is a player.
     * @returns Returns false if it fails and logs an error.
     */
    fun msgTo(puppetNumber: Int, message: String): Boolean{
        var success = true
        val connectionNumber = puppetList.getPuppetListener(puppetNumber)

        if (connectionNumber > -1){
            if (connectionNumber < MAX_CONNECTIONS){
                success = mudOutput.addMsg(connectionNumber, message)
                if (!success){
                    log.logError("Failed to add Msg to mudOutput:\n  World.msgTo(${puppetNumber.toString()}, $message)")
                }
            }
            else {
                log.logError("Connection number(${connectionNumber.toString()}) out of range:\n  World.msgTo(${puppetNumber.toString()}, $message)")
                success = false
            }
        }
        return success
    }

    /**
     * Sends a message to all players in a [Room].
     *
     * @param roomNumber The number of the room to send a message.
     * @param message The message to send.
     * @param exclude (Optional)Do not send message for a certain puppetNumber, usually they are sent a slightly different one.
     * @returns Returns false if at least one message fails to be added in the [MsgQueue].
     */
    fun msgRoom(roomNumber: Int, message: String, exclude: Int = -1): Boolean{
        var success = true
        val puppets = roomList.puppetsInRoom(roomNumber)

        for(puppet in puppets){
            if(puppet != exclude){
                if(msgTo(puppet, message) == false){
                    success = false
                }
            }
        }
        if(!success){
            log.logError("Failed to broadcast message in room:\n  World.msgRoom(${roomNumber.toString()}, $message, ${exclude.toString()})")
        }
        return success
    }

    /**
     * Allows a player to look around a room.
     *
     * This calls some functions and adds a [Msg] to mudOutput of the [Room] and who is in it. It checks
     * to see if there were any extra arguments by checking if the leftover string passed is an empty string.
     * A badly typed command still returns true but responds with a message about extra arguments. 
     *
     * @param puppetNumber The puppet that is looking.
     * @param leftover (Optional)If anything else was after "look" in the command.
     * @returns Returns false if something fails.
     */
    fun lookAround(puppetNumber: Int, leftover: String = ""): Boolean{
        var success = false
        var outputString: String
        var location: Int

        //Returns -1 if playerNumber is out of range or notAlive
        location = puppetList.getPuppetLocation(puppetNumber)
        if(location >= 0){
            if (leftover == ""){
                outputString = roomList.lookAtRoom(location)
                if (outputString == "Room number is out of range.\n"){
                    log.logError("Bad location:\n  World.lookAround(${puppetNumber.toString()})")
                }
                outputString += puppetList.lookAtPuppetList(roomList.puppetsInRoom(location), puppetNumber)
                success = msgTo(puppetNumber, outputString)
            }
            else {
                //Still considered a success even though player typed the wrong thing.
                success = msgTo(puppetNumber, "Look command doesn't take any arguments.")
            }
            if (!success){
                log.logError("Failed to add Msg to mudOutput: \n  World.lookAround(${puppetNumber.toString()})")
            }
        }
        else {
            log.logError("Player number out of range or not alive:\n  World.lookAround(${puppetNumber.toString()})")
        }
        return success
    }

    /**
     * Adds a new puppet, sets its listener to connectionNumber, sets its name to name and returns the puppet number.
     * Returning the puppet number here is important because it is grabbed and passed to the connection object to allowed
     * the commands the player types to control this new puppet.
     *
     * @param connectionNumber The number to set this puppet's listener to.
     * @param name The name of the player.
     * @returns The puppet number.
     */
    fun insertNewPlayer(connectionNumber: Int, name: String): Int{
        var puppetNumber = -1
        var room: Int

        if(connectionNumber < MAX_CONNECTIONS){
            //This looks kinda messy to me. I must have been up late or something. I vaguely feel like some value needs to be checked.
            puppetNumber = puppetList.newPlayerPuppet(connectionNumber, name, defaultHome)
            room = puppetList.getPuppetLocation(puppetNumber)
            roomList.addPuppetToRoom(room, puppetNumber)
            msgRoom(room, name + " suddenly pops into existence!", puppetNumber)
            lookAround(puppetNumber)
        }
        else {
            log.logError("Connection number out of range:\n  World.insertNewPlayer(${connectionNumber.toString()}, $name)")
        }
        if(puppetNumber == -1){
            log.logError("Insert new player failed: \n  World.insertNewPlayer(${connectionNumber.toString()}, $name)")
        }
        return puppetNumber
    }

    /**
     * This removes a player from [PuppetList] and [RoomList].
     *
     * @param puppetNumber The number of the [Puppet] to be removed.
     * @returns True or false.
     */
    fun removePlayer(puppetNumber: Int): Boolean{
        var success = false
        var room: Int
        var name: String

        if(puppetList.isPuppetNumberInRange(puppetNumber)){
            //getPuppetLocation returns -1 if puppetNumber is out of range or puppet is dead
            room = puppetList.getPuppetLocation(puppetNumber)
            if(room != -1){
                name = puppetList.getPuppetName(puppetNumber)
                if(puppetList.killPuppet(puppetNumber)){
                    success = roomList.removePuppetFromRoom(room, puppetNumber)
                    msgRoom(room, name + " turns to dust.")
                }
            }
        }
        if(!success){
            log.logError("Failed to remove player: \n  removePlayer(" + puppetNumber.toString() + ")")
        }
        return success
    }

    /**
     * This adds a puppet to a room you are in.
     *
     * @param puppetNumber Your puppet number.
     * @param name The name of the new puppet.
     */
    fun newPuppet(puppetNumber: Int, name: String): Int{
        var newPuppetNumber = -1
        var room: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        if(room != -1){
            newPuppetNumber = puppetList.newPuppet(name, room)
        }
        if(newPuppetNumber == -1){
            msgTo(puppetNumber, "Creating new puppet failed.")
        }
        else{
            //insert to room and success msg
            roomList.addPuppetToRoom(room, newPuppetNumber)
            //There should be a message to everyone in the room.
            msgTo(puppetNumber, "Created new puppet (${newPuppetNumber.toString()}).")
        }
        return newPuppetNumber
    }

    /**
     * What can I say about this? Say something everyone in the same room can hear.
     *
     * @param puppetNumber Puppet number of the speaker.
     * @param message What you say.
     * @returns True or false.
     */
    fun say(puppetNumber: Int, message: String): Boolean{
        var success = false
        val location: Int

        location = puppetList.getPuppetLocation(puppetNumber)
        if(location > -1){
            msgTo(puppetNumber, "You say '" + message + "'")
            msgRoom(puppetList.getPuppetLocation(puppetNumber), puppetList.getPuppetName(puppetNumber) + " says '" + message + "'", puppetNumber)
            success = true
        }
        if(!success){
            log.logError("Failed to speak:\n  World.say(${puppetNumber.toString()}, $message)")
        }
        return success
    }

    /**
     * After checking input from a player to see if it is a command, the next question to ask is isThisThingAnExit?
     *
     * @param puppetNumber To find the room you are in.
     * @param thing A string that might be the name of an exit.
     * @returns isThisThingAnExit?
     */
    fun isThisThingAnExit(puppetNumber: Int, thing: String): Boolean{
        var success = false
        val location = puppetList.getPuppetLocation(puppetNumber)
        val shortDirectionIndex = shortDirectionArray.indexOf(thing)

        //There was a check if this was an empty string so I'm not checking it again.
        if (shortDirectionIndex != -1){
            if (roomList.whatRoomExitGoesTo(location, directionArray[shortDirectionIndex]) > -1){
                success = true
            }
        }
        else {
            if (roomList.whatRoomExitGoesTo(location, thing) > -1){
                success = true
            }
        }
        return success
    }

    /**
     * Moves a [Puppet] from one [Room] to another [Room].
     *
     * This should still return true if the exitName is not an exit in the room.
     *
     * @parameter puppetNumber The puppet to move.
     * @exitName The name of the [Exit] to another [Room].
     * @returns Returns false if something fails.
     */
    fun move(puppetNumber: Int, exitName: String): Boolean{
        //Double check to make sure a wrong exitName isn't going to trigger error message
        //Commander just rejects anything that's not a command and has leftovers.
        var success = false
        var moveFrom: Int
        var moveTo: Int
        val shortDirectionIndex = shortDirectionArray.indexOf(exitName)

        moveFrom = puppetList.getPuppetLocation(puppetNumber)
        if (moveFrom >= 0){
            //Check if exitName is a short direction.
            if (shortDirectionIndex == -1){
                moveTo = roomList.whatRoomExitGoesTo(moveFrom, exitName)
            }
            else {
                moveTo = roomList.whatRoomExitGoesTo(moveFrom, directionArray[shortDirectionIndex])
            }
            if (moveTo >= 0){
                //If it got to this point, puppetNumber and exitName are correct
                success = true
                if (!roomList.removePuppetFromRoom(moveFrom, puppetNumber)){
                    success = false
                }
                //message about leaving and arriving
                if (!roomList.addPuppetToRoom(moveTo, puppetNumber)){
                    success = false
                }
                if (!puppetList.changePuppetLocation(puppetNumber, moveTo)){
                    success = false
                }
                msgTo(puppetNumber, "You go $exitName.")
                msgRoom(moveFrom, "${puppetList.getPuppetName(puppetNumber)} goes $exitName.")
                msgRoom(moveTo, "${puppetList.getPuppetName(puppetNumber)} arrives.", puppetNumber)
                lookAround(puppetNumber)
            }
            else {
                msgTo(puppetNumber, "You cannot go that way.")
            }
        }
        if (!success){
            log.logError("Failed to move puppet:  \n  World.move(${puppetNumber.toString()}, $exitName)")
        }
        return success
    }

    /**
     * Creates a new room and a pair of exits to connect them.
     *
     * If no rooms have been created yet, the parameters don't matter. Otherwise, you need only exitName if the name of
     * the exit is one of the common ones in directionArray. If the exit name is not one of the eight compass directions
     * or up or down or in or out, then you will need to give a string for the name of the exit in the new room to connect
     * with the room you are currently in.
     */
    fun makeRoom(puppetNumber: Int, exitName: String = "", exitBackToThisRoom: String = "", leftovers: String = ""): Int{
        //Check if creating rooms is allowed
        var newRoomNumber = -1
        var directionNumber: Int
        val location = puppetList.getPuppetLocation(puppetNumber)

        if (leftovers == ""){
            if (location != -1){
                if(roomList.numberOfRooms == 0){
                    newRoomNumber = roomList.newRoom(location, "", "")
                    puppetList.changePuppetLocation(puppetNumber, newRoomNumber)
                    roomList.addPuppetToRoom(newRoomNumber, puppetNumber)
                }
                else {
                    if (exitBackToThisRoom == ""){
                        //see if exitName is in array and get the opposite direction
                        directionNumber = directionArray.indexOf(exitName)
                        if (directionNumber != -1){
                            //get opposite direction and call roomList.newRoom()
                            directionNumber += rotateValue
                            if (directionNumber >= directionArray.size){
                                directionNumber -= directionArray.size
                            }
                            newRoomNumber = roomList.newRoom(location, exitName, directionArray[directionNumber])
                        }
                        else {
                            msgTo(puppetNumber, "You need to either pick a direction from the default list or supply a return exit name.")
                        }
                    }
                    else {
                        if (exitName != ""){
                            newRoomNumber = roomList.newRoom(location, exitName, exitBackToThisRoom)
                        }
                        else {
                            //This is an empty string followed by a not empty string, this shouldn't happen.
                            log.logError("Failed to make room: \n  World.makeRoom(${puppetNumber.toString()}, $exitName, $exitBackToThisRoom)")
                        }
                    }
                }
            }
            else {
                //getPuppetLocation failed somehow
                log.logError("Failed to make room, bad location: \n  World.makeRoom(${puppetNumber.toString()}, $exitName, $exitBackToThisRoom)")
            }
            if (newRoomNumber != -1){
                msgTo(puppetNumber, "Created Room (${newRoomNumber.toString()}).")
            }
        }
        else {
            msgTo(puppetNumber, "Too many arguments.")
        }
        return newRoomNumber
    }

    /**
     * Change the name of the [Room] you are in.
     *
     * @param puppetNumber Your puppetNumber.
     * @param name The new name of the [Room].
     * @returns Returns false if it fails.
     */
    fun changeRoomName(puppetNumber: Int, name: String): Boolean{
        var success = false
        var room: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        if(room != -1){
            success = roomList.changeRoomName(room, name)
        }
        if(success){
            //msg of success
            msgTo(puppetNumber, "Room name changed.")
        }
        else{
            //log error
            log.logError("Change room name failed:\n  World.changeRoomName(${puppetNumber.toString()}, $name)")
        }
        return success
    }

    /**
     * Changes description of the [Room] you are in.
     *
     * @param puppetNumber Your puppetNumber.
     * @param desc The new description.
     * @returns Returns false if it fails.
     */
    fun changeRoomDescription(puppetNumber: Int, desc: String): Boolean{
        var success = false
        var room: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        if(room != -1){
            success = roomList.changeRoomDescription(room, desc)
        }
        if(success){
            //msg of success
            msgTo(puppetNumber, "Room description changed.")
        }
        else{
            //log error
            log.logError("Change room description failed:\n  World.changeRoomDescription(${puppetNumber.toString()}, $desc)")
        }
        return success
    }

}
