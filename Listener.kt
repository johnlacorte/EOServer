import java.net.*

//pass in port number and ConnectionContainer array
class Listener(connectionArray: ConnectionContainer, port: Int) : Runnable{
    val isRunning : Boolean
    val serverSocket : ServerSocket
    val connections : Array<ConnectionContainer>
    //member variable to hold the thing new client connections are passed to
    init{
        isRunning = true
        serverSocket = ServerSocket(port)
    }

    fun run(){
        while(isRunning){
            //If I kill serverSocket, will it return null or what?
            //I would need to handle that null somehow
            //Check if there's room for one more
            connections.newConnect(serverSocket.accept())
        }
    }

    fun kill(){
        isRunning = false
        //close serverSocket
        serverSocket.close()
    }
}
