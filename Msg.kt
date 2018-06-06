//Msg class holds a string for input or output for connected clients
class Msg(number: Int, message: String){
    val playerNumber = number
    val messageString = message

    fun look(): String{
        return "(" + playerNumber.toString() + ") " + messageString + "\n"
    }
}