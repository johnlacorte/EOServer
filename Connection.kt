//Pass in some sort of log, checked for banned IP addresses, getIPAddress()
//might need to set input and output to null for old sockets to go away
//Do I want to grab the time connected?
import java.net.*
import java.io.*

/**
 * This holds all the stuff needed to communicate across the network with one client and a few things for
 * status of the connection and for creating Msgs for mudInput MsgQueue.
 *
 * @param clientNumber The number of this particular connection object.
 * @param bufferSize The size of the buffer to store characters coming from connected client.
 * @property clientConnected Would you expect this client is still connected unless you found out differently? Default = false.
 * @property clientPuppetNumber The number of the puppet that takes commands from this connection. Default = -1.
 * @property clientSocket The socket passed by the Listener. Default = Socket() (Empty socket).
 * @property clientName The name of the player connected. Default = "ClientName".
 * @property inputString This is a StringBuffer that stores characters until a newline is encountered.
 */
class Connection(val clientNumber: Int, bufferSize: Int){
    var clientConnected: Boolean
    var clientPuppetNumber: Int
    var clientSocket: Socket
    var clientName: String
    var inputString: StringBuffer

    init{
        clientConnected = false
        clientPuppetNumber = -1
        clientSocket = Socket()
        clientName = "ClientName"
        inputString = StringBuffer(bufferSize)
    }

    /**
     * This returns the clientName for now. I would like it to give a lot more information eventually, like IP address or
     * time connected.
     */
    fun look(): String{
        //val retString = clientSocket.toString()
        return clientName
    }
}
