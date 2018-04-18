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
            //Don't do this, create a thread to handle logging in and pass in connections
            //to it. Also, if I kill serverSocket, will it return null or what?
            //I would need to handle that null somehow
            connections.newConnect(serverSocket.accept())
        }
    }

    fun kill(){
        isRunning = false
        //close serverSocket
        serverSocket.close()
    }
}
