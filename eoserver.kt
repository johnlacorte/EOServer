//World class that holds rooms, connections, puppets, items, whatever
//Start World thread, start Listener thread, and then periodically wake up to
//check everything is going ok. Make sure that functions accessed return a value
//to make sure it is waiting until things are finished before moving on
//including kill() functions
import java.net.*

fun main(args: Array<String>) {
    //set up Room and Puppet arrays
    //check if port is a number too
    if(args.size == 1){
        //create ConnectionContainer
        //create Listener thread
        //online mode should require an area file
        val portNumber = args[0].toInt()
        val serverSocket = ServerSocket(portNumber)
        val clientSocket = Thread(Connection(serverSocket.accept()))
        clientSocket.start()
    }
    //if not started with a port start in offline mode
    //offline mode could start you in ghost mode with one room and a command to load an area
    else{
        println("Usage: eoserver <port number>")
    }
    //main loop
}
