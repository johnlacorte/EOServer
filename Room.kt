//Room number is the only thing necessary for creating new rooms I would
//like other constructors to pass in data for new rooms or load from file

class Room(number: Int){

    val roomNumber: Int
    var roomName: String
    var roomDescription: String
    var roomExits: ExitList
    var roomPuppets: PuppetsInRoom

    init{
        roomNumber = number
        roomName = "New Room"
        roomDescription = "There is nothing here"
        roomExits = ExitList()
        roomPuppets = PuppetsInRoom()
    }

    fun look() : String{
        //Returns a string to describe a room
         var retString = roomName + "\n" + roomDescription + "\n\n" + roomExits.look()
         retString = retString + "\n"//Extra line before listing puppets or items
         return retString
    }

    //fun save()
    //fun load()
}
