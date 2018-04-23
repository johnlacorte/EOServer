//Should be able to add invisibility by adding a boolean to constructor and returning
//an empty string, if seeing invisibility is a thing, add a boolean to look() for see
//invisible
class PuppetInRoom(number: Int, name: String, description: String){
    val puppetNumber: Int
    val puppetName: String
    var puppetDescription: String

    init{
        puppetNumber = number
        puppetName = name
        puppetDescription = description
    }

    fun look(number: Int): String{
        //When looking in a room you don't see yourself
        if(number == puppetNumber){
            return ""
        }
        else{
            return puppetName + puppetDescription + "\n"
        }
    }
}
