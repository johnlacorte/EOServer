//Pass in some sort of log, checked for banned IP addresses, getIPAddress()
//add synchronized modifier to most of these methods
//might need to set input and output to null for old sockets to go away
import java.net.*
import java.io.*

class Connection(n: Int){
    val number: Int
    var connected: Boolean
    var clientSocket: Socket
    var clientName: String
    var outputWriter: PrintWriter
    var inputReader: InputStreamReader
    var inputString: StringBuffer

    init{
        number = n
        connected = false
    }

    fun newConnect(socket: Socket, name: String){
        //check the stuff to make sure it all worked
        //check to see if it is already connected
        clientSocket = socket
        clientName = name
        inputReader = InputStreamReader(clientSocket.getInputStream())
        outputWriter = PrintWriter(clientSocket.getOutputStream(), true)
        connected = true
        inputString = StringBuffer(2000)
    }

    fun isConnected(): Boolean{
        return connected
    }

    fun clientOut(outputString: String): Boolean{
        //use word wrap here
        if(clientSocket.isConnected()){
            outputWriter.println(outputString)
            return true
        }
        else{
            connected = false
            return false
        }
    }

    fun isThereInput(): Pair<Boolean, Boolean>{
        //maybe check the status of the connection
        //check for client input, add it to a string, and return a boolean
        var readChar: Char
        var stillConnected: Boolean
        var fullLineOfText: Boolean
        if(clientSocket.isConnected){
            stillConnected = true
            //read in a character at a time
            while(inputReader.ready())
            {
                readChar = inputReader.read()
                //check for too many characters in buffer or newline
                if(readChar == '\n'){
                    fullLineOfText = true
                }
                else{
                    inputString.append(readChar)
                    if(inputString.length() > 2000){
                        //break connection, flush buffer, log it, return false
                        kill("Too many characters of input at once. Disconnecting.")
                        stillConnected = false
                        fullLineOfText = false
                        break
                    }
                }
            }
        }
        else{
            stillConnected = false
            fullLineOfText = false
        }
        return Pair(stillConnected, fullLineOfText)
     }

    fun clientIn(): String{
        val retString = inputString.toString()
        inputString = StringBuffer(2000)
        return retString
    }

    fun kill(msg: String = ""){
        clientOut(msg)
        clientOut("Disconnected from Server.")
        connected = false
        clientSocket.close()
    }
}
