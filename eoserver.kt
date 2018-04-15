//World class that holds rooms, connections, puppets, items, whatever
import java.net.*

fun main(args: Array<String>) {
    //check if port is a number too
    if(args.size == 1){
        val portNumber = args[0].toInt()
        val serverSocket = ServerSocket(portNumber)
        val clientSocket = Thread(Connection(serverSocket.accept()))
        clientSocket.start()
    }
    else{
        println("Usage: eoserver <port number>")
    }
}