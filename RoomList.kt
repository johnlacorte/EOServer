//clearList()?
/**
 * RoomList is a collection of [Room] objects. May or may not be a linked list.
 *
 * @param log A [Logger] object for error messages.
 * @property numberOfRooms The number of rooms, initialized to zero.
 */
class RoomList(var log: Logger){
    var numberOfRooms: Int
    var roomList: MutableList<Room>

    init{
        numberOfRooms = 0
        roomList = mutableListOf()
    }

    /**
     * How I check if a [Room] with a certain roomNumber exists.
     *
     * @param roomNumber A number to be checked.
     * @returns False if roomNumber is less than 0 or greater than the number of the last [Room] created. 
     */
    fun isRoomNumberInRange(roomNumber: Int): Boolean{
        if (roomNumber >= 0 && roomNumber < numberOfRooms){
            return true
        }
        else {
            return false
        }
    }

    /**
     * Calls [Room.look()] on a particular room.
     *
     * If no rooms have been created, this returns a special message. If the room number is out of range,
     * this returns a string "Room number out of range" and logs an error message. Otherwise, calls [Room.look()].
     *
     * @param roomNumber The number of the room you want to look() at.
     * @returns A string that ends in a newline.
     */
    fun lookAtRoom(roomNumber: Int) : String{
        var retString: String

        if (isRoomNumberInRange(roomNumber)){
            retString = roomList[roomNumber].look()
        }
        else {
            if(numberOfRooms == 0){
                retString = "There is nothing here!\nYou are floating in some wibbly wobbly time-y wimey stuff\nNothing to do but load an area or create a new room\n"
            }
            else {
                log.logError("Room number is out of range: \n  RoomList.lookAtRoom(${roomNumber.toString()})")
                retString = "Room number is out of range.\n"
            }
        }
        return retString
    }

    /**
     * Creates a new room and creates exits between it and the room you are currently in.
     *
     * If you are creating the first room, the only thing you require is a value for startRoom and that
     * doesn't even matter what it is because you aren't in any room. Logs the error "Failed to create a new room"
     * if it fails.
     *
     * @param startRoom The room that gets an [Exit] for the new room.
     * @param exitName The name of the [Exit] that connects to the new room.
     * @param exitBackToThisRoom The name of the [Exit] in the new room that brings you back to the current room.
     * @returns The number of the newly created room or -1 if it fails.
     */
    fun newRoom(startRoom: Int, exitName: String = "", exitBackToThisRoom: String = ""): Int{
        var retValue = -1

        if (numberOfRooms == 0){
            roomList.add(Room(numberOfRooms))
            retValue = numberOfRooms
            numberOfRooms++
        }
        else {
            if (isRoomNumberInRange(startRoom) && exitName != "" && exitBackToThisRoom != ""){
                //If exits fail to be created a room might end up cut off from everything else.
                roomList.add(Room(numberOfRooms))
                roomList[startRoom].roomExits.addExit(exitName, numberOfRooms)
                roomList[numberOfRooms].roomExits.addExit(exitBackToThisRoom, startRoom)
                retValue = numberOfRooms
                numberOfRooms++
            }
            else {
                log.logError("Failed to create a new room: \n  RoomList.newRoom(${startRoom.toString()}, $exitName, $exitBackToThisRoom)")
            }
        }
        return retValue
    }

    /**
     * Changes the name of a room or logs the error "Room number is out of range".
     *
     * @param roomNumber The number of an existing room.
     * @param newName The new name for the room.
     * @returns Returns false and logs an error if it fails.
     */
    fun changeRoomName(roomNumber: Int, newName: String): Boolean{
        //Should probably check if the newName is an empty string or something.
        var success = false

        if (isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomName = newName
            success = true
        }
        else {
            log.logError("Room number is out of range: \n  RoomList.changeRoomName(${roomNumber.toString()}, $newName)")
        }
        return success
    }

    /**
     * Changes the description of a room or logs the error "Room number is out of range".
     *
     * @param roomNumber The number of the room you want to change the description of.
     * @param newDescription The new description for the room.
     * @returns Returns false if it fails and logs an error message.
     */
    fun changeRoomDescription(roomNumber: Int, newDescription: String): Boolean{
        //Should probably check if the newDescription is an empty string or something.
        var success = false

        if (isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomDescription = newDescription
            success = true
        }
        else {
            log.logError("Room number is out of range: \n  RoomList.changeRoomDescription(${roomNumber.toString()}, $newDescription)")
        }
        return success
    }

    /**
     * Adds an [Exit] to the [ExitList] in a [Room].
     *
     * This logs errors "Failed to add exit" or "Room number is out of range" along with the function and stuff
     * if it fails.
     * 
     * @param roomNumber The number of the room to add the exit to.
     * @param exitName The name of the new exit.
     * @param exitRoom The number of the room the exit connects to.
     * @returns Returns false if it fails.
     */
    fun addExitToRoom(roomNumber: Int, exitName: String, exitRoom: Int): Boolean{
         var success = false

        if(isRoomNumberInRange(roomNumber) && isRoomNumberInRange(exitRoom)){
            //addExit() returns false if passed empty string or the name of an exit that exists
            if (roomList[roomNumber].roomExits.addExit(exitName, exitRoom)){
                success = true
            }
            else {
                log.logError("Failed to add exit")
            }
        }
        else {
            log.logError("Room number is out of range: \n  RoomList.addExitToRoom(${roomNumber.toString()}, $exitName, ${exitRoom.toString()})")
        }
        return success
    }

    /**
     * Removes an [Exit] from the [ExitList] in a [Room].
     *
     * This logs errors "Failed to remove exit" or "Room number out of range" along with the function name
     * and stuff if it fails.
     *
     * @param roomNumber The number of the room to remove an exit from.
     * @param exitName The name of the exit to remove.
     * @returns Returns false if it fails.
     */
    fun removeExitFromRoom(roomNumber: Int, exitName: String): Boolean{
        var success = false

        if(isRoomNumberInRange(roomNumber)){
            //removeExit() returns false if exit is not found
            if (roomList[roomNumber].roomExits.removeExit(exitName)){
                success = true
            }
            else {
                log.logError("Failed to remove exit")
            }
        }
        else{
            log.logError("Room number is out of range: \n  RoomList.removeExitFromRoom(${roomNumber.toString()}, $exitName)")
        }
        return success
    }

    /**
     * Gets the number of a room an [Exit] goes to.
     *
     * This function returns a room number or -1. A negative return value can indicate either
     * the room number passed in to check for the exit is out of range OR exit was not found. It is
     * pretty easy to decide what the cause was from the context it was used or by checking the log
     * file for "Room number is out of range" message.
     *
     * @param roomNumber The number of the room you want to check the exit in.
     * @param exitName The name of the exit.
     * @returns A room number or -1 if none.
     */
    fun whatRoomExitGoesTo(roomNumber: Int, exitName: String): Int{
        var retValue = -1

        if (isRoomNumberInRange(roomNumber)){
            //getExitRoom() returns -1 if it fails to find it
            retValue = roomList[roomNumber].roomExits.getExitRoom(exitName)
        }
        else {
            log.logError("Room number is out of range: \n  RoomList.whatRoomExitGoesTo(${roomNumber.toString()}, $exitName)")
        }
        return retValue
    }

    /**
     * Adds a puppet number to a room.
     *
     * If this fails, it can log "Attempted to add a puppetNumber already in a room" or "Room number out of range".
     * This does absolutely nothing if no rooms have been created yet to allow a player to enter a MUD with no rooms
     * and start building.
     *
     * @param roomNumber The number of the [Room] to add the puppet number to.
     * @param puppetNumber The number to add to the room.
     * @returns Returns false if it fails.
     */
    fun addPuppetToRoom(roomNumber: Int, puppetNumber: Int): Boolean{
        var success = false

        if (isRoomNumberInRange(roomNumber)){
            //addPuppet() returns false if puppet is already there
            success = roomList[roomNumber].puppetsInRoom.add(puppetNumber)
            if (!success){
                log.logError("Attempted to add a puppetNumber already in a room: \n  RoomList.addPuppetToRoom(${roomNumber.toString()}, ${puppetNumber.toString()})")
            }
        }
        else {
            if (numberOfRooms == 0){
                //Just ignore it if no rooms have been created yet
                success = true
            }
            else {
                log.logError("Room number is out of range: \n  RoomList.addPuppetToRoom(${roomNumber.toString()}, ${puppetNumber.toString()})")
            }
        }
        return success
    }

    /**
     * Removes puppetNumber from room's puppetList.
     *
     * If this fails, it can log "Unable to remove puppet from room, puppet not in room" or
     * "Room number is out of range".
     *
     * @param roomNumber The number of the room you want to remove a puppet number from.
     * @param puppetNumber The puppet number to remove from the room.
     * @returns Returns false if it fails.
     */
    fun removePuppetFromRoom(roomNumber: Int, puppetNumber: Int): Boolean{
        var success = false

        if (isRoomNumberInRange(roomNumber)){
            //removePuppet() returns false if puppet wasn't here already
            success = roomList[roomNumber].puppetsInRoom.remove(puppetNumber)
            if (!success){
                log.logError("Unable to remove puppet from room, puppet not in room: \n  RoomList.removePuppetFromRoom(${roomNumber.toString()}, ${puppetNumber.toString()})")
            }
        }
        else {
            log.logError("Room number is out of range: \n  RoomList.removePuppetFromRoom(${roomNumber.toString()}, ${puppetNumber.toString()})")
        }
        return success
    }

    /**
     * Get a list of numbers of puppets in the room from puppetsInRoom.
     *
     * If no rooms have been created yet, this returns an empty list. If it fails it returns an empty
     * list and logs "Room number out of range". I've given it a lot of thought and I couldn't come up
     * with a situation where I would want a list of puppet numbers from an empty room so you could probably
     * safely assume that an empty list returned from this function when there are more than zero rooms is
     * an error.
     *
     * @param roomNumber The room you want to get a list of puppet numbers from.
     * @returns Returns a list of integers.
     */
    fun puppetsInRoom(roomNumber: Int): List<Int>{
        val retList: List<Int>

        if (numberOfRooms != 0){
            if (isRoomNumberInRange(roomNumber)){
                retList = roomList[roomNumber].puppetsInRoom.toList()
            }
            else {
                //Returns an empty list if room number is out of range
                retList = listOf()
                log.logError("Room number out of range: \n  RoomList.puppetsInRoom(${roomNumber.toString()})")
            }
        }
        else {
            //if number of rooms == 0
            retList = listOf()
        }
        return retList
    }
}
