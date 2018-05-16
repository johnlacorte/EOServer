//Constructor needs to take zero or more exits
class ExitList(){
    var exitList: MutableList<Exit>

    init{
        exitList = mutableListOf()
    }

    fun look(): String{
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
        //Maybe arrange these into order
        var success = true

        if(findExit(dir) == null){
            exitList.add(Exit(dir, room))
        }
        else{
            success = false
        }
        return success
    }

    fun removeExit(exitName: String): Boolean{
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
        var retValue = -1
        val exit = findExit(exitName)
        if(exit != null){
            retValue = exit.roomNumber
        }
        return retValue
    }

}
