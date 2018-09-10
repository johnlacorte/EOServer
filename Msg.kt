/**
 * A Msg is an object with a number and a string for member variables.
 *
 * A Msg is a package for passing around strings between the parts that handle network connections and the
 * parts that keep track of the state of the MUD. The number is the number of the connection for output or
 * number of the puppet for commands that come in.
 *
 * @param number The number of the thing the message is intended for.
 * @param message The message.
 */
class Msg(val number: Int, val message: String){

    /**
     * I added this to so I don't have to write a bunch of print statements for testing.
     *
     * @returns A string that looks something like "(3) Hello World!\n"
     */
    fun look(): String{
        return "(${number.toString()}) $message\n"
    }
}