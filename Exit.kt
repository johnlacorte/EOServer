/**
 * An Exit is used to look up the room number going a certain direction will bring you to.
 *
 * An exit is used to connect rooms in the MUD. I plan on adding a door feature in the future
 * and I wrote it as a new class instead of using some other standard data structure to be able
 * to add on without modifying the rest of the program other than writing a couple of new functions.
 *
 * @param exitName This is the name of the exit.
 * @param roomNumber This is the number of the room that this exit points to.
 * @constructor Creates a new Exit and sets the name and room number.
 */
class Exit(val exitName: String, val roomNumber: Int){

    /**
     * Currently this only returns the name of this exit.
     *
     * Returns a string representation of this exit. This is used rather than just getting the
     * direction property so "(closed)" or "(locked)" can be added in the future without breaking
     * anything or changing any existing code.
     * @returns A string representation of this exit.
     */
    fun look(): String{
        return exitName
    }
}
