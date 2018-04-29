//Adding doors is simply adding some booleans, changing look() function, and adding
//additional functions like isClosed(), openDoor(), closeDoor(), etc

class Exit(dir: String, room: Int){

    val direction: String
    val roomNumber: Int

    init{
        direction = dir
        roomNumber = room
    }

    fun look(): String{
        //This function is only for displaying the direction in an exit list
        //that way I can keep it seperate in case I add more details in this view
        //like if is an open or closed door.
        return direction
    }
}
