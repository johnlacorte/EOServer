//World is a bridge between rooms contained in a RoomContainer and puppets
//This class will change as the vision and features change
//This should take an argument to decide to start with an empty world or
//load files maybe one for whether it is is offline mode
//Commands: newroom, roomname, roomdescription, chat, newpuppet, clone, puppetname, puppetlistener, say, look, quit
//Need to add checks for empty strings and negative numbers cause thats what the command functions are going to get

class World(howManyConnections: Int, logger: Logger){
    //I need functions for everything you can do
    var MAX_CONNECTIONS: Int
    var roomList: RoomList
    var puppetList: PuppetList
    var mudOutput: MsgQueue
    val log = logger
    val defaultHome = 0
    val directionArray = arrayOf("in", "north", "northeast", "northwest", "up", "west", "out", "south", "southwest", "southeast", "down", "east")
    val rotateValue = 6

    init{
        MAX_CONNECTIONS = howManyConnections
        roomList = RoomList(log)
        puppetList = PuppetList(log)
        mudOutput = MsgQueue(log)
    }

    fun msgTo(puppetNumber: Int, message: String): Boolean{
        //check against howmanyconnections
        var success = false
        val connectionNumber = puppetList.getPuppetListener(puppetNumber)

        if(connectionNumber > -1 && connectionNumber < MAX_CONNECTIONS){
            success = mudOutput.addMsg(connectionNumber, message)
        }
        if(!success){
            log.logError("Failed to send message:\n  " + Msg(puppetNumber, message).look())
        }
        return success
    }

    fun msgRoom(room: Int, message: String, exclude: Int = -1): Boolean{

        var success = true
        val puppets = roomList.puppetsInRoom(room)

        for(puppet in puppets){
            if(puppet != exclude){
                if(msgTo(puppet, message) == false){
                    success = false
                }
            }
        }
        if(!success){
            log.logError("Failed to broadcast message:\n  msgRoom(" + room.toString() + ", " + message + ")")
        }
        return success
    }

    fun lookAround(puppetNumber: Int, leftover: String = ""): Boolean{
        var success = false
        var outputString = ""
        var location: Int

        //Returns -1 if playerNumber is out of range or notAlive
        location = puppetList.getPuppetLocation(puppetNumber)
        if(location >= 0){
            outputString += roomList.lookAtRoom(location)
            outputString += puppetList.lookAtPuppetList(roomList.puppetsInRoom(location), puppetNumber)
            success = msgTo(puppetNumber, outputString)
        }
        if(!success){
            log.logError("Look failed: \n  lookAround(" + puppetNumber.toString() + ")")
        }
        return success
    }

    fun insertNewPlayer(connectionNumber: Int, name: String): Int{
        var puppetNumber = -1
        var room: Int

        if(connectionNumber < MAX_CONNECTIONS){
            puppetNumber = puppetList.newPlayerPuppet(connectionNumber, name, defaultHome)
            room = puppetList.getPuppetLocation(puppetNumber)
            roomList.addPuppetToRoom(room, puppetNumber)
            msgRoom(room, name + " suddenly pops into existence!", puppetNumber)
            lookAround(puppetNumber)
        }
        if(puppetNumber == -1){
            log.logError("Insert new player failed: \n  insertNewPlayer(" + connectionNumber.toString() + ", " + name + ")")
        }
        return puppetNumber
    }

    fun removePlayer(puppetNumber: Int): Boolean{
        //comments here
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

    fun newPuppet(puppetNumber: Int, name: String): Int{
        var newPuppetNumber = -1
        var room: Int
        var connectionNumber: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        //not really sure why this is here and what I planned to do with connection number
        connectionNumber = puppetList.getPuppetListener(puppetNumber)
        if(room != -1){
            newPuppetNumber = puppetList.newPuppet(name, room)
        }
        if(newPuppetNumber == -1){
            msgTo(puppetNumber, "Creating new puppet failed.")
        }
        else{
            //insert to room and success msg
            roomList.addPuppetToRoom(room, newPuppetNumber)
            msgTo(puppetNumber, "Created new puppet " + newPuppetNumber.toString() + ".")
        }
        return newPuppetNumber
    }

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
            log.logError("Failed to speak:\n  say(" + puppetNumber.toString() + ", " + message +")")
        }
        return success
    }

    fun move(puppetNumber: Int, exitName: String, leftover: String = ""): Boolean{
        //Create leaving and arriving messages and no exit messages
        //Double check to make sure a wrong exitName isn't going to trigger error message
        var success = false
        var moveFrom: Int
        var moveTo: Int

        moveFrom = puppetList.getPuppetLocation(puppetNumber)
        if(moveFrom >= 0){
            moveTo = roomList.whatRoomExitGoesTo(moveFrom, exitName)
            if(moveTo >= 0){
                //If it got to this point, puppetNumber and exitName are correct
                success = true
                if(!roomList.removePuppetFromRoom(moveFrom, puppetNumber)){
                    success = false
                }
                //message about leaving and arriving
                if(!roomList.addPuppetToRoom(moveTo, puppetNumber)){
                    success = false
                }
                if(!puppetList.changePuppetLocation(puppetNumber, moveTo)){
                    success = false
                }
                msgTo(puppetNumber, "You go " + exitName + ".")
                msgRoom(moveFrom, puppetList.getPuppetName(puppetNumber) + " goes " + exitName + ".")
                msgRoom(moveTo, puppetList.getPuppetName(puppetNumber) + " arrives.", puppetNumber)
                lookAround(puppetNumber)
            }
            else{
                msgTo(puppetNumber, "You cannot go that way.")
            }
        }
        if(!success){
            log.logError("Failed to move puppet:  \n  move(" + puppetNumber.toString() + ", " + exitName + ")")
        }
        return success
    }

    fun makeRoom(puppetNumber: Int, exitName: String = "", exitBackToThisRoom: String = "", leftovers: String = ""): Int{
        //Check if creating rooms is allowed
        var newRoomNumber = -1
        var directionNumber: Int
        val location = puppetList.getPuppetLocation(puppetNumber)

        if(location != -1){
            if(roomList.numberOfRooms == 0){
                newRoomNumber = roomList.newRoom(location, "", "")
                puppetList.changePuppetLocation(puppetNumber, newRoomNumber)
                roomList.addPuppetToRoom(newRoomNumber, puppetNumber)
            }
            else{
                if(exitBackToThisRoom == ""){
                    //see if exitName is in array and get the opposite direction
                    directionNumber = directionArray.indexOf(exitName)
                    if(directionNumber != -1){
                        //get opposite direction and call roomList.newRoom()
                        directionNumber += rotateValue
                        if(directionNumber >= directionArray.size){
                            directionNumber -= directionArray.size
                        }
                        newRoomNumber = roomList.newRoom(location, exitName, directionArray[directionNumber])
                    }
                    else{
                        //need to use a default direction or give a return exit name
                        msgTo(puppetNumber, "You need to either pick a direction from the default list or supply a return exit name.")
                    }
                }
                else{
                    if(exitName != ""){
                        newRoomNumber = roomList.newRoom(location, exitName, exitBackToThisRoom)
                    }
                    else{
                        //This could only happen if whatever called this function messed up
                        log.logError("Failed to make room: \n  makeRoom(" + puppetNumber.toString() + ", " + exitName + ", " + exitBackToThisRoom + ")")
                    }
                }
            }
        }
        else{
            //getPuppetLocation failed somehow
            log.logError("Failed to make room, bad location: \n  makeRoom(" + puppetNumber.toString() + ", " + exitName + ", " + exitBackToThisRoom + ")")
        }
        if(newRoomNumber != -1){
            msgTo(puppetNumber, "Created Room " + newRoomNumber.toString() + ".")
        }
        return newRoomNumber
    }

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
            log.logError("Change room name failed:\n  changeRoomName(" + puppetNumber.toString() + ", " + name + ")")
        }
        return success
    }

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
            log.logError("Change room description failed:\n  changeRoomDescription(" + puppetNumber.toString() + ", " + desc + ")")
        }
        return success
    }

}
