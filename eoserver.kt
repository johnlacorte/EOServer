//World class that holds rooms, connections, puppets, items, whatever
//Start World thread, start Listener thread, and then periodically wake up to
//check everything is going ok. Make sure that functions accessed return a value
//to make sure it is waiting until things are finished before moving on
//including kill() functions
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
