//Room number is the only thing necessary for manually creating rooms, if
//you wanted something silly like a way to generate dungeons you can
class Room(number: Int,
            name: String = "New Room",
            description: String = "There is nothing here.",
            parentExit: Exit = null,
            ){
    val roomNumber: Int
    var roomName: String
    var roomDescription: String
    var roomExits: MutableList<Exit> = mutableListOf()
    var roomPuppets: MutableList<PuppetInRoom> = mutableListOf()

    init{
        roomNumber = number
        roomName = name
        roomDescription = description
        if(parentExit != null){
            roomExitRooms.add(parentExit)
        }
    }
    //look(), addPuppet(), removePuppet(), changeName(), changeDescription(), addExit(),
    //removeExit(), puppetNumbers() (to get a list of numbers for sending output),
    //save(), load()
    //I might start with passing a string to all these at the beginning
    fun look(puppetNumber: Int) : String{
        var retString = roomName + "\n\n" + roomDescription + "\n\nExits:\n"
        if(roomExits.size > 0){
            for(exit in roomExits){
                retString = retString + exit.look() + " "
            }
            retString = retString + "\n"//Extra line before listing puppets or items
        }
        else{
            retString = retString + "none\n\n"//Extra line before listing puppets or items
        }
        for(puppet in roomPuppets){
            val description = puppet.look(puppetNumber)
            if(description != ""){
                retString = retString + puppet.look(puppetNumber) + "\n"
            }
        }
        return retString
    }

    fun addPuppet(puppet: PuppetInRoom){
        roomPuppets.add(puppet)
    }

    //fun removePuppet(){
    //I don't know if I should pass a PuppetInRoom or puppet number
    //}

    fun changeName(newName: String){
        roomName = newName
    }

    fun changeDescription(newDescription: String){
        roomDescription = newDescription
    }

    fun addExit(exit: Exit){
        roomExitRooms.add(exit)
    }

    //fun removeExit()
    //fun puppetNumbers()
    //fun save()
    //fun load()
}
