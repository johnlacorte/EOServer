import java.util.LinkedList
import java.util.Queue

/**
 * A queue to hold messages that have not been processed yet.
 *
 * I tried to find a queue in Kotlin's standard library and strangely didn't find anything. I had to borrow
 * Java's LinkedList and Queue interface which I wasn't happy about. I want to either find or write one for
 * Kotlin once I have a good idea of what my requirements are.
 *
 * @param log The Logger for error messages.
 */
class MsgQueue(var log: Logger){
    var msgQueue: Queue<Msg> = LinkedList<Msg>()

    /**
     * Adds a [Msg] to the MsgQueue.
     *
     * @param number The number to be passed to the [Msg] constructor.
     * @param message The message to be passed to the [Msg] constructor.
     * @returns Returns false if it fails.
     */
    fun addMsg(number: Int, message: String): Boolean{
        var success: Boolean

        success = msgQueue.offer(Msg(number, message))
        if(!success){
            log.logError("Unable to add message to message queue: \n  ${Msg(number, message).look()}")
        }
        return success
    }

    /**
     * Gets a [Msg] maybe or a null from MsgQueue.
     *
     * @returns Msg object or null.
     */
    fun getMsg(): Msg?{
        return msgQueue.poll()
    }
}