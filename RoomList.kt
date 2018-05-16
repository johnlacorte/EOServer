//This should take some kind of argument to decide to start with an empty list or
//load files
//It might be helpful for some of the functions that are caused by a command
//to return a string indicating what happened
//Logging of events starts here
//Room list might perform better as a map or vector

class RoomList(howManyConnections: Int){

    val MAX_CONNECTIONS: Int
    var numberOfRooms: Int
    var roomList: MutableList<Room>

    init{
        MAX_CONNECTIONS = howManyConnections
        numberOfRooms = 0
        roomList = mutableListOf()
    }

    fun newRoom(): Int{
        if(numberOfRooms == 0){
            roomList = mutableListOf()
        }
        roomList.add(Room(numberOfRooms))
        numberOfRooms++
        return numberOfRooms - 1
    }

    fun isRoomNumberInRange(number: Int): Boolean{
        //Checks if there is a room that exists for the number passed in
        //This can be changed to take advantage of the functional stuff from kotlin
        if(number >= 0 && number < numberOfRooms){
            return true
        }
        else{
            return false
        }
    }

    fun isPuppetNumberInRange(number: Int): Boolean{
        //Checks if there is a puppet that exists for the number passed in
        if(number >= 0 && number < MAX_CONNECTIONS){
            return true
        }
        else{
            return false
        }
    }

    fun lookAtRoom(roomNumber: Int) : String{
        //Change this to tell the difference between numberOfRooms = 0 and roomNumber
        //out of range
        if(isRoomNumberInRange(roomNumber)){
            return roomList[roomNumber].look()
        }
        else{
            return "There is nothing here!\nYou are floating in some wibbly wobbly time-y wimey stuff\nNothing to do but load an area or create a new room\n"
        }
    }

    fun changeRoomName(roomNumber: Int, newName: String){
        //Changes the name of a room in RoomContainer
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomName = newName
        }
    }

    fun changeRoomDescription(roomNumber: Int, newDescription: String){
        //Changes the name of a room in RoomContainer
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomDescription = newDescription
        }
    }

    fun addExitToRoom(roomNumber: Int, exitName: String, exitRoom: Int){
        //Adds exit to room in RoomContainer
        //Check exitRoom too
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomExits.addExit(exitName, exitRoom)
        }
    }

    fun removeExitFromRoom(roomNumber: Int, exitName: String){
        //Removes exit from room in RoomContainer
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomExits.removeExit(exitName)
        }
    }

    fun addPuppetToRoom(roomNumber: Int, puppet: Int){
        //Check puppet number
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomPuppets.addPuppet(puppet)
        }
    }

    fun removePuppetFromRoom(roomNumber: Int, puppet: Int){
        //Check puppet number
        //Return value
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].roomPuppets.removePuppet(puppet)
        }
    }

    fun puppetsInRoom(roomNumber: Int): List<Int>{
        //Returns a list of what puppets are in a room
        
        val retList: List<Int>
        if(isRoomNumberInRange(roomNumber)){
            retList = roomList[roomNumber].roomPuppets.puppetNumbers()
        }
        else{
            retList = listOf()
        }
        return retList
    }

    //fun save()
    //fun load()
}
