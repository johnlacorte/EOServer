//individual connections to client, checks for new messages from client and puppet and
//goes to sleep
//can I just put all the connections in one thread and go through them manually?
//Please do not extend Thread. Instead, implement Runnable and then 
//call new Thread(runnable).start() to start the thread. Or even better, use an 
//Executor or ExecutorService to automatically manage threads in pools.
//Maybe add word wrap in here
import java.net.*
import java.io.*

class Connection(socket: Socket) : Runnable{
    val mSocket: Socket
    val mOutputWriter: PrintWriter
    val mInputReader: BufferedReader

    init{
        mSocket = socket
        mOutputWriter = PrintWriter(socket.getOutputStream(), true)
        mInputReader = BufferedReader(InputStreamReader(socket.getInputStream()))
    }

    override fun run(){
        var inputLine = mInputReader.readLine()
        while (inputLine != null) {
            mOutputWriter.println(inputLine)
            inputLine = mInputReader.readLine()
        }
    }

}