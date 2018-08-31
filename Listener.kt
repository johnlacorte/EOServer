import java.net.*
import java.io.*
//I don't know what is going to happen when I call kill() I think I should try it, call killAll() from
//ConnectionList and try to check the status of this thread and forcibly end it if necessary
//pass in port number and ConnectionList
class Listener(connectionlist: ConnectionList, port: Int) : Runnable{
    var isRunning : Boolean
    var serverSocket : ServerSocket
    var connectionList : ConnectionList
    //member variable to hold the thing new client connections are passed to
    init{
        isRunning = true
        serverSocket = ServerSocket(port)
        connectionList = connectionlist
    }

    override fun run(){
        while(isRunning){
            //If I kill serverSocket, will it return null or what?
            //I would need to handle that null somehow
            //Check if there's room for one more
            connectionList.newConnection(serverSocket.accept())
        }
    }

    //fun kill(){
    //    isRunning = false
        //close serverSocket
    //    serverSocket.close()
    //}
}
