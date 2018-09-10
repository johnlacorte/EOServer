/**
 * A Room in the MUD.
 *
 * A Room object stores data about a room including name, descriptions, exits, and Puppets in the room for
 * easy lookup. The roomNumber isn't used for anything except the constructor, I added it in early on expecting
 * it might be useful someday if I create a new data structure to store rooms or perhaps some new feature that 
 * I haven't thought of. There probably will be a second constructor to set name and description to be used when
 * loading a room from a file or if there is ever a need to automatically generate areas.
 * @param roomNumber The number of the new room.
 * @property roomName The name of the room given on the first line of the string returned by Room.look().
 * @property roomDescription Long detailed description of the Room
 * @property roomExits The [ExitList] of the room.
 * @property puppetsInRoom a mutable set of Int to hold puppet numbers for easy lookup.
 * @constructor Creates a Room object with the name "New Room" and description "There is nothing here".
 */
class Room(val roomNumber: Int){

    var roomName: String
    var roomDescription: String
    var roomExits: ExitList
    var puppetsInRoom: MutableSet<Int>

    init{
        roomName = "New Room"
        roomDescription = "There is nothing here"
        roomExits = ExitList()
        puppetsInRoom = mutableSetOf()
    }

    /**
     * Returns a string made from roomName, roomDescription, and the return string from calling roomExits.look()
     * @returns A string with the room exits seperated from the other parts by an empty line.
     */
    fun look() : String{
         return roomName + "\n" + roomDescription + "\n\n" + roomExits.look() + "\n"
    }

}
