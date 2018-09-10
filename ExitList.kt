//TODO If DOORS exist add functions to open and close them. 
/**
 * All the exits for a Room and all of the member functions to see, create, find, and remove.
 *
 * The ExitList class creates an empty list of [Exit]. Each element added must have a unique name and may
 * not be an empty string or it returns false. Obviously, elements that are not in the list cannot be removed
 * and this will return false as well. 
 */
class ExitList(){
    var exitList: MutableList<Exit>

    init{
        exitList = mutableListOf()
    }

    /**
     * Private function used by other member functions to find an [Exit] by name.
     *
     * @param exitName the string to search for in exitList.
     * @returns An [Exit] or null if not found.
     */
    private fun findExit(name: String): Exit?{
        var retExit: Exit? = null

        for (exit in exitList){
            if (exit.exitName == name){
                retExit = exit
            }
        }
        return retExit
    }

    /**
     * When you want to show a Room's Exits.
     *
     * @returns A string. "Exits:", a new line , the strings returned by [Exit.look()] for each [Exit] seperated by a space, and a new line.
     */
    fun look(): String{
        var retString = "Exits:\n"

        if (exitList.size > 0){
            for (exit in exitList){
                retString = retString + exit.look() + " "
            }
            retString = retString + "\n"
        }
        else {
            retString = "Exits:\nnone\n"
        }
        return retString
    }

    /**
     * Construct a new [Exit] and add it to exitList.
     *
     * @param name A string that will be the name of the new [Exit].
     * @param room An Int for the number of the Room this [Exit] will take you to.
     * @returns False if an [Exit] with this name already exists or name is an empty string.
     */
    fun addExit(name: String, room: Int): Boolean{
        //TODO I would like it to insert these into exitList in an order that makes it easier to read at a glance.
        var success = false

        if (name != ""){
            if (findExit(name) == null){
                exitList.add(Exit(name, room))
                success = true
            }
        }
        return success
    }

    /**
     * Finds an [Exit] by name and removes it from exitList.
     *
     * @param name A string that will be searched for.
     * @returns False if an [Exit] with the name exitName is not found.
     */
    fun removeExit(name: String): Boolean{
        var success = true
        val exit = findExit(name)

        if (exit == null){
            success = false
        }
        else {
            exitList.remove(exit)
        }
        return success
    }

    /**
     * Finds an [Exit] by name in exitList and returns it's roomNumber value.
     *
     * @param name A string that will be searched for.
     * @returns [Exit] found's roomNumber or -1 if not found.
     */
    fun getExitRoom(name: String): Int{
        //TODO if DOORS are a thing, return -1 if the door is closed. This will result in a "You cannot go that way." message
        //the same as if there was no exit of that name found.
        var retValue = -1
        val exit = findExit(name)

        if (exit != null){
            retValue = exit.roomNumber
        }
        return retValue
    }
}
