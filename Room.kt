//parent stuff could be optional, room number is probably necessary
class Room(number: Int,
            name: String = "New Room",
            description: String = "There is nothing here.",
            parentExit: Int = 0,
            parentDirection: String = ""){
    var mNumber: Int
    var mName: String
    var mDescription: String
    var mExitRooms: MutableList<Int> = mutableListOf()
    var mExitNames: MutableList<String> = mutableListOf()
    init{
        mNumber = number
        mName = name
        mDescription = description
        if(parentExit != 0 && parentDirection != ""){
            mExitRooms.add(parentExit)
            mExitNames.add(parentDirection)
        }
    }
    //look(), addPuppet(), removePuppet(), changeName(), changeDescription(), addExit()
    //I might start with passing a string to all these at the beginning
    fun look() : String{
        var retString = mName + "\n\n" + mDescription + "\n\nExits:\n"
        if(mExitNames.size > 0){
            for(){
                retString = retString + mExitNames[i] + " "
            }
            retString = retString + "\n"//Extra line before listing puppets or items
        }
        else{
            retString = retString + "None\n"//Extra line before listing puppets or items
        }
        return retString
    } 
}
