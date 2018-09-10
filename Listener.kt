import java.net.*
import java.io.*

/**
 * A Listener object runs in its own thread and waits for new connections to server.
 *
 * @param connectionList This is the ConnectionList created when the server started up.
 * @param port This is the port number to Listen for new connections.
 * @property isRunning This is checked after every new connection.
 * @property serverSocket The ServerSocket.
 */
class Listener(var connectionList: ConnectionList, port: Int) : Runnable{
    var isRunning : Boolean
    var serverSocket : ServerSocket

    init{
        isRunning = true
        serverSocket = ServerSocket(port)
    }

    /**
     * This is the stuff that runs in its own thread.
     */
    override fun run(){
        while(isRunning){
            connectionList.newConnection(serverSocket.accept())
        }
    }
}
