//World is a bridge between rooms contained in a RoomContainer and puppets
//This class will change as the vision and features change
//This should take an argument to decide to start with an empty world or
//load files maybe one for whether it is is offline mode
//Commands: newroom, roomname, roomdescription, chat, newpuppet, clone, puppetname, puppetlistener
//Maybe create a message for others in the room when a player creates a room or a puppet

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
        //log = Logger()
        roomList = RoomList(log)
        puppetList = PuppetList(log)
        mudOutput = MsgQueue(log)
    }

    fun msgTo(connectionNumber: Int, message: String): Boolean{
        //check against howmanyconnections
        var success = false

        if(connectionNumber < MAX_CONNECTIONS){
            success = mudOutput.addMsg(connectionNumber, message)
        }
        if(!success){
            log.logError("Failed to send message:\n  " + Msg(connectionNumber, message).look())
        }
        return success
    }

    fun msgRoom(room: Int, message: String, exclude: Int = -1): Boolean{
        //msgAll() and msgAllButOne() should probably take a list of Int to be more useful

        var success = true
        val puppets = roomList.puppetsInRoom(room)
        var connectionNumber: Int

        for(puppet in puppets){
            if(puppet != exclude){
                connectionNumber = puppetList.getPuppetListener(puppet)
                if(connectionNumber > -1){
                    if(msgTo(connectionNumber, message) == false){
                        success = false
                    }
                }
            }
        }
        if(!success){
            log.logError("Failed to broadcast message:\n  msgRoom(" + room.toString() + ", " + message + ")")
        }
        return success
    }

    fun lookAround(puppetNumber: Int): Boolean{
        var success = false
        var outputString = ""
        var location: Int

        //Returns -1 if playerNumber is out of range or notAlive
        location = puppetList.getPuppetLocation(puppetNumber)
        if(location >= 0){
            outputString += roomList.lookAtRoom(location)
            outputString += puppetList.lookAtPuppetList(roomList.puppetsInRoom(location), puppetNumber)
            success = msgTo(puppetList.getPuppetListener(puppetNumber), outputString)
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
        //should I check room and name values?
        var success: Boolean
        var room: Int
        var name: String

        room = puppetList.getPuppetLocation(puppetNumber)
        name = puppetList.getPuppetName(puppetNumber)
        success = puppetList.killPuppet(puppetNumber)
        if(success){
            success = roomList.removePuppetFromRoom(room, puppetNumber)
            msgRoom(room, name + " turns to dust.")
        }
        else{
            log.logError("Failed to remove player: \n  removePlayer(" + puppetNumber.toString() + ")")
        }
        return success
    }

    fun newPuppet(puppetNumber: Int, name: String): Int{
        var newPuppetNumber = -1
        var room: Int
        var connectionNumber: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        connectionNumber = puppetList.getPuppetListener(puppetNumber)
        if(room != -1){
            newPuppetNumber = puppetList.newPuppet(name, room)
        }
        if(newPuppetNumber == -1){
            msgTo(connectionNumber, "Creating new puppet failed.")
        }
        else{
            //insert to room and success msg
            roomList.addPuppetToRoom(room, newPuppetNumber)
            msgTo(connectionNumber, "Created new puppet " + newPuppetNumber.toString() + ".")
        }
        return newPuppetNumber
    }

    fun say(puppetNumber: Int, message: String): Boolean{
        var success = false
        val connectionNumber: Int
        val location: Int

        connectionNumber = puppetList.getPuppetListener(puppetNumber)
        if(connectionNumber > -1){
            location = puppetList.getPuppetLocation(puppetNumber)
            if(location > -1){
                msgTo(connectionNumber, "You say '" + message + "'")
                msgRoom(puppetList.getPuppetLocation(puppetNumber), puppetList.getPuppetName(puppetNumber) + " says '" + message + "'", puppetNumber)
                success = true
            }
        }
        if(!success){
            log.logError("Failed to speak:\n  say(" + puppetNumber.toString() + ", " + message +")")
        }
        return success
    }

    fun move(puppetNumber: Int, exitName: String): Boolean{
        //Create leaving and arriving messages and no exit messages
        //Double check to make sure a wrong exitName isn't going to trigger error message
        var success = false
        var moveFrom: Int
        var moveTo: Int
        val connectionNumber: Int

        connectionNumber = puppetList.getPuppetListener(puppetNumber)
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
                msgTo(connectionNumber, "You go " + exitName + ".")
                msgRoom(moveFrom, puppetList.getPuppetName(puppetNumber) + " goes " + exitName + ".")
                msgRoom(moveTo, puppetList.getPuppetName(puppetNumber) + " arrives.", connectionNumber)
                lookAround(puppetNumber)
            }
            else{
                msgTo(connectionNumber, "You cannot go that way.")
            }
        }
        if(!success){
            log.logError("Failed to move puppet:  \n  move(" + puppetNumber.toString() + ", " + exitName + ")")
        }
        return success
    }

    fun makeRoom(puppetNumber: Int, exitName: String = "", exitBackToThisRoom: String = ""): Int{
        //Check if creating rooms is allowed
        var newRoomNumber = -1
        var directionNumber: Int
        val location = puppetList.getPuppetLocation(puppetNumber)
        val connectionNumber = puppetList.getPuppetListener(puppetNumber)

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
                        msgTo(connectionNumber, "You need to either pick a direction from the default list or supply a return exit name.")
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
            msgTo(connectionNumber, "Created Room " + newRoomNumber.toString() + ".")
        }
        return newRoomNumber
    }

    fun changeRoomName(puppetNumber: Int, name: String): Boolean{
        var success = false
        var room: Int
        var connectionNumber: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        connectionNumber = puppetList.getPuppetListener(puppetNumber)
        if(room != -1){
            success = roomList.changeRoomName(room, name)
        }
        if(success){
            //msg of success
            msgTo(connectionNumber, "Room name changed.")
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
        var connectionNumber: Int

        room = puppetList.getPuppetLocation(puppetNumber)
        connectionNumber = puppetList.getPuppetListener(puppetNumber)
        if(room != -1){
            success = roomList.changeRoomDescription(room, desc)
        }
        if(success){
            //msg of success
            msgTo(connectionNumber, "Room description changed.")
        }
        else{
            //log error
            log.logError("Change room description failed:\n  changeRoomDescription(" + puppetNumber.toString() + ", " + desc + ")")
        }
        return success
    }

    //} String.split(' ')
}
