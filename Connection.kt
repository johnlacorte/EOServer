//Pass in some sort of log, checked for banned IP addresses, getIPAddress()
//add synchronized modifier to most of these methods
//might need to set input and output to null for old sockets to go away

//Do I want to grab the time connected?

import java.net.*
import java.io.*

class Connection(connectionNumber: Int, bufferSize: Int){
    var clientNumber: Int
    var clientConnected: Boolean
    var clientPuppetNumber: Int
    var clientSocket: Socket
    var clientName: String
    //var outputWriter: PrintWriter
    //var inputReader: InputStreamReader
    var inputString: StringBuffer

    init{
        clientNumber = connectionNumber
        clientConnected = false
        clientPuppetNumber = -1
        clientSocket = Socket()
        clientName = "ClientName"
        //outputWriter = PrintWriter(clientSocket.getOutputStream(), true)
        //inputReader = InputStreamReader(clientSocket.getInputStream())
        inputString = StringBuffer(bufferSize)
    }

    fun look(): String{
        //val retString = clientSocket.toString()
        return clientName
    }
}
