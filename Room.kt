//parent stuff could be optional, room number is probably necessary
class Room constructor(number: Int, name: String, description: String, parentExit: Int, parentDirection: String){
    var mNumber: Int
    var mName: String
    var mDescription: String
    var mExitRooms: MutableList<Int> = mutableListOf()
    var mExitNames: MutableList<String> = mutableListOf()
    init{
        mNumber = number
        mName = name
        mDescription = description
        mExitRooms.add(parentExit)
        mExitNames.add(parentDirection)
    }
}