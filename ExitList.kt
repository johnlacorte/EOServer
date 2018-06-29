//Constructor needs to take zero or more exits

class ExitList(){
    var exitList: MutableList<Exit>

    init{
        exitList = mutableListOf()
    }

    fun look(): String{
        //Returns a string of all the Exits of this room
        var retString = "Exits:\n"

        if(exitList.size > 0){
            for(exit in exitList){
                retString = retString + exit.look() + " "
            }
            retString = retString + "\n"
        }
        else{
            retString = "Exits:\nnone\n"
        }
        return retString
    }

    fun findExit(exitName: String): Exit?{
        //Finds if an Exit with a certain name is here
        var retExit: Exit? = null

        for(exit in exitList){
            //check if exitname matches and set retExit to it if true
            if(exit.direction == exitName){
                retExit = exit
            }
        }
        return retExit
    }

    fun addExit(dir: String, room: Int): Boolean{
        //Adds an Exit to a Room. I would like it to arrange these into an order that makes it easier to read.
        var success = false

        if(dir != ""){
            if(findExit(dir) == null){
                exitList.add(Exit(dir, room))
                success = true
            }
        }
        return success
    }

    fun removeExit(exitName: String): Boolean{
        //Removes an Exit when given the name of Exit
        var success = true
        val exit = findExit(exitName)

        if(exit == null){
            success = false
        }
        else{
            exitList.remove(exit)
        }
        return success
    }

    fun getExitRoom(exitName: String): Int{
        //Returns the Room an Exit leads to given the name of the Exit
        var retValue = -1
        val exit = findExit(exitName)

        if(exit != null){
            retValue = exit.roomNumber
        }
        return retValue
    }
}
