//MsgQueue class holds Msg objects and returns them in a first in first out order
//I might implement my own version of a queue if I don't find a Kotlin version of it. This
//feels a bit messy and too easy to break.
//isThereANewMsg() probably isnt needed when I can just check for null
import java.util.LinkedList
import java.util.Queue

class MsgQueue(logger: Logger){
    var msgQueue: Queue<Msg> = LinkedList<Msg>()
    var log = logger

    //fun isThereANewMsg(): Boolean{
    //    var newMessage = false
    //    //maybe check msgQueue.size() instead of .peek()
        //for some reason .size() doesn't work with a Queue in Kotlin
    //    if(msgQueue.peek() != null){
    //        newMessage = true
    //    }
    //    return newMessage
    //}

    fun addMsg(number: Int, message: String): Boolean{
        //log error
        var success: Boolean

        success = msgQueue.offer(Msg(number, message))
        if(!success){
            log.logError("Unable to add message to message queue: \n  " + Msg(number, message).look())
        }
        return success
    }

    fun getMsg(): Msg?{
        //Do I need Msg? instead of Msg
        return msgQueue.poll()
    }
}