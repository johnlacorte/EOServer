//This should take some kind of argument to decide to start with an empty list or
//load files
//It might be helpful for some of the functions that are caused by a command
//to return a string indicating what happened
//Logging of events starts here

class RoomContainer(howManyConnections: Int){

    val MAX_CONNECTIONS: Int
    var numberOfRooms: Int
    var roomList: MutableList<Room>

    init{
        MAX_CONNECTIONS = howManyConnections
        numberOfRooms = 0
        roomList = mutableListOf()
    }

    fun newRoom(name: String = "New Room",
                description: String = "There is nothing here.",
                parentExit: MutableList<Exit> = mutableListOf()): Int{
        //Creates a new room, parentExit is the initial list of Exits, usually
        //this will contain only an exit back to the room it was created from
        //but if you wanted to start off with more exits it would be easy.
        //Return the new rooms id
        if(numberOfRooms == 0){
            roomList = mutableListOf()
        }
        roomList.add(Room(numberOfRooms, name, description, parentExit))
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

    fun addPuppetToRoom(roomNumber: Int, puppet: Int){
        //Check puppet number
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].addPuppet(puppet)
        }
    }

    fun removePuppetFromRoom(roomNumber: Int, puppet: Int){
        //Check puppet number
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].removePuppet(puppet)
        }
    }

    fun changeRoomName(roomNumber: Int, newName: String){
        //Changes the name of a room in RoomContainer
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].changeName(newName)
        }
    }

    fun changeRoomDescription(roomNumber: Int, newDescription: String){
        //Changes the name of a room in RoomContainer
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].changeDescription(newDescription)
        }
    }

    fun addExitToRoom(roomNumber: Int, exit: Exit){
        //Adds exit to room in RoomContainer
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].addExit(exit)
        }
    }

    fun removeExitFromRoom(roomNumber: Int, exitName: String){
        //Removes exit from room in RoomContainer
        if(isRoomNumberInRange(roomNumber)){
            roomList[roomNumber].removeExit(exitName)
        }
    }

    fun puppetsInRoom(roomNumber: Int): List<Int>{
        //Returns a list of what puppets are in a room
        if(isRoomNumberInRange(roomNumber)){
            return roomList[roomNumber].puppetNumbers()
        }
        else{
            return listOf()
        }
    }

    //fun save()
    //fun load()
}
