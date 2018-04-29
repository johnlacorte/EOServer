//Room number is the only thing necessary for creating rooms, if
//you wanted something silly like a way to generate dungeons you can
//by passing in the details

class Room(number: Int,
            name: String = "New Room",
            description: String = "There is nothing here.",
            parentExit: MutableList<Exit> = mutableListOf()
            ){

    val roomNumber: Int
    var roomName: String
    var roomDescription: String
    var roomExits: MutableList<Exit>
    var roomPuppets: MutableList<Int>

    init{
        roomNumber = number
        roomName = name
        roomDescription = description
        roomExits = parentExit
        roomPuppets = mutableListOf()
    }

    //look(), addPuppet(), removePuppet(), changeName(), changeDescription(), addExit(),
    //removeExit(), puppetNumbers() (to get a list of numbers for sending output),
    //save(), load()

    fun look() : String{
        //Returns a string to describe a room
        var retString = roomName + "\n" + roomDescription + "\n\nExits:\n"
        if(roomExits.size > 0){
            for(exit in roomExits){
                retString = retString + exit.look() + " "
            }
            retString = retString + "\n\n"//Extra line before listing puppets or items
        }
        else{
            retString = retString + "none\n\n"//Extra line before listing puppets or items
        }
        return retString
    }

    fun addPuppet(puppet: Int){
        //Add a puppets id number to a room
        //Do I want to check for duplicates?
        roomPuppets.add(puppet)
    }

    fun removePuppet(puppet: Int){
        //Removes a puppets id number from a room
        roomPuppets.remove(puppet)
    }

    fun changeName(newName: String){
        //Changes the name of the room
        roomName = newName
    }

    fun changeDescription(newDescription: String){
        //Changes the description of the room
        roomDescription = newDescription
    }

    fun addExit(exit: Exit){
        //Adds an exit to the room
        //Check for duplicates maybe return a value to indicate it
        roomExits.add(exit)
    }

    fun removeExit(exitName: String){
        //Removes an exit from a room by passing in the exit's name
        //Maybe return a value to indicate if exit was found
        for(exit in roomExits){
            if(exit.direction == exitName){
                roomExits.remove(exit)
            }
        }
    }

    fun puppetNumbers(): List<Int>{
        //Return a list of puppet id numbers currently in the room
        var numbers: MutableList<Int> = mutableListOf()
        for(puppet in roomPuppets){
            numbers.add(puppet)
        }
        return numbers.toList()
    }
    //fun save()
    //fun load()
}
