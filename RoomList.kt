//This should take some kind of argument to decide to start with an empty list or
//load files
//It might be helpful for some of the functions that are caused by a command
//to return a string indicating what happened
//Logging of events starts here for functions that automatically add rooms or exits or puppets
//Room list might perform better as a map or vector
//clearList()
//change roomPuppets to playerPuppetsInRoom here and in Room.kt

class RoomList(logger: Logger){

    var numberOfRooms: Int
    var roomList: MutableList<Room>
    var log = logger

    init{
        numberOfRooms = 0
        roomList = mutableListOf()
    }

    fun isRoomNumberInRange(number: Int): Boolean{
        //Checks if there is a room that exists for the number passed in
        if(number >= 0 && number < numberOfRooms){
            return true
        }
        else{
            return false
        }
    }

    fun lookAtRoom(roomNumber: Int) : String{
        //Returns a string with name, description, and exits if room exists
        //log error if numberOfRooms > 0
        var retString = ""
        if(isRoomNumberInRange(roomNumber)){
            retString = roomList[roomNumber].look()
        }
        else{
            if(numberOfRooms == 0){
                retString = "There is nothing here!\nYou are floating in some wibbly wobbly time-y wimey stuff\nNothing to do but load an area or create a new room\n"
            }
            else{
                log.logError("Room number out of range: \n  lookAtRoom(" + roomNumber.toString() + ")")
            }
        }
        return retString
    }

    fun newRoom(startRoom: Int, exitName: String = "", exitBackToThisRoom: String = ""): Int{
        //Creates a new room and adds exits between them, returns number of new room or -1
        //Creating the first room needs only startRoom which doesn't actually matter what it is
        //log error
        var retValue = -1

        if(numberOfRooms == 0 || isRoomNumberInRange(startRoom) && exitName != "" && exitBackToThisRoom != ""){
            roomList.add(Room(numberOfRooms))
            roomList[startRoom].roomExits.addExit(exitName, numberOfRooms)
            roomList[numberOfRooms].roomExits.addExit(exitBackToThisRoom, startRoom)
            retValue = numberOfRooms
            numberOfRooms++
        }
        else{
            log.logError("Failed to create a new room: \n  addRoom(" + startRoom.toString() + ", " + exitName + ", " + exitBackToThisRoom + ")")
        }
        return retValue
    }

    fun changeRoomName(roomNumber: Int, newName: String): Boolean{
        //Changes the name of a room in RoomList, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomName = newName
            success = true
        }
        else{
            log.logError("Room number out of range: \n  changeRoomName(" + roomNumber.toString() + ", " + newName + ")")
        }
        return success
    }

    fun changeRoomDescription(roomNumber: Int, newDescription: String): Boolean{
        //Changes the name of a room in RoomList, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomDescription = newDescription
            success = true
        }
        else{
            log.logError("Room number out of range: \n  changeRoomDescription(" + roomNumber.toString() + ", " + newDescription + ")")
        }
        return success
    }

    fun addExitToRoom(roomNumber: Int, exitName: String, exitRoom: Int): Boolean{
        //Adds exit to room in RoomList, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber) && isRoomNumberInRange(exitRoom)){
            //addExit() returns false if passed empty string or the name of an exit that exists
            success = roomList[roomNumber].roomExits.addExit(exitName, exitRoom)
        }
        else{
            log.logError("Room number is out of range: \n  addExitToRoom(" + roomNumber.toString() + ", " + exitName + ", " + exitRoom.toString() + ")")
        }
        return success
    }

    fun removeExitFromRoom(roomNumber: Int, exitName: String): Boolean{
        //Removes exit from room in RoomList, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber)){
            //removeExit() returns false if exit is not found
            success = roomList[roomNumber].roomExits.removeExit(exitName)
        }
        else{
            log.logError("Room number is out of range: \n  removeExitFromRoom(" + roomNumber.toString() + ", " + exitName + ")")
        }
        return success
    }

    fun whatRoomExitGoesTo(roomNumber: Int, exitName: String): Int{
        //Returns the room number of an exit or -1 if something fails
        //log error
        var retValue = -1
        if(isRoomNumberInRange(roomNumber)){
            //getExitRoom() returns -1 if it fails to find it
            retValue = roomList[roomNumber].roomExits.getExitRoom(exitName)
        }
        else{
            log.logError("Room number is out of range: \n  whatRoomExitGoesTo(" + roomNumber.toString() + ", " + exitName + ")")
        }
        return retValue
    }

    fun addPuppetToRoom(roomNumber: Int, puppet: Int): Boolean{
        //Adds a puppet to a room's PuppetsInRoom, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber)){
            //addPuppet() returns false if puppet is already there
            success = roomList[roomNumber].roomPuppets.addPuppet(puppet)
            if(!success){
                log.logError("Multiple attempts to add a puppet to a room.")
            }
        }
        else{
            log.logError("Room number is out of range: \n  addPuppetToRoom(" + roomNumber.toString() + ", " + puppet.toString() + ")")
        }
        return success
    }

    fun removePuppetFromRoom(roomNumber: Int, puppet: Int): Boolean{
        //Removes a puppet from a room's PuppetsInRoom, returns true if successful
        //log error
        var success = false
        if(isRoomNumberInRange(roomNumber)){
            //removePuppet() returns false if puppet wasn't here already
            success = roomList[roomNumber].roomPuppets.removePuppet(puppet)
            if(!success){
                log.logError("Unable to remove puppet from room, puppet not in room.")
            }
        }
        else{
            log.logError("Room number is out of range: \n  removePuppetFromRoom(" + roomNumber.toString() + ", " + puppet.toString() + ")")
        }
        return success
    }

    fun puppetsInRoom(roomNumber: Int): List<Int>{
        //Returns a list of what puppets are in a room
        //log error in the else block
        val retList: List<Int>
        if(isRoomNumberInRange(roomNumber)){
            retList = roomList[roomNumber].roomPuppets.puppetNumbers()
        }
        //Returns an empty list if room number is out of range
        else{
            retList = listOf()
            log.logError("Room number out of range: \n  puppetsInRoom(" + roomNumber.toString() + ")")
        }
        return retList
    }

    //fun save()
    //fun load()
}