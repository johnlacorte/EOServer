//Adding doors is simply adding a boolean, changing look() function, and adding
//additional functions isClosed(), openDoor(), closeDoor()
class Exit(dir: String, room: Int){
    val direction: String
    val roomNumber: Int
    init{
        direction = dir
        roomNumber = room
    }

    fun look(): String{
        //this function is only for displaying the direction in an exit list
        //that way I can keep it seperate in case I add more details in this view
        return direction
    }
}
