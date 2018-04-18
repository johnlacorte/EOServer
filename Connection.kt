import java.net.*
import java.io.*

class Connection(){
    var connected: Boolean
    var clientSocket: Socket
    var clientName: String
    var outputWriter: PrintWriter
    var inputReader: InputStreamReader
    var linesOfInput: Int
    var inputString: StringBuffer

    init{
        connected = false
    }

    fun newConnect(socket: Socket, name: String){
        clientSocket = socket
        clientName = name
        inputReader = InputStreamReader(clientSocket.getInputStream())
        outputWriter = PrintWriter(clientSocket.getOutputStream(), true)
        connected = true
        linesOfInput = 0
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

    fun isInput(): Boolean{
        //maybe check the status of the connection
        //check for client input, add it to a string, and return a boolean
        var readChar: char
        if(clientSocket.isConnected){
            //read in a character at a time
            while(inputReader.ready())
            {
                readChar = inputReader.read()
                //check for too many characters in buffer or newline
                if(readChar == '\n'){
                    return true
                }
                else{
                    inputString.append(readChar)
                    if(inputString.length() > 2000){
                        //break connection, flush buffer, log it, return false
                        kill("Too many characters of input at once. Disconnecting.")
                    }
                }
            }
        }
        return false
     }

    fun getString(): String{
        val retString = inputString.toString()
        inputString = StringBuffer(2000)
        return retString
    }

    fun kill(msg: String = ""){
        connected = false
        clientSocket.close()
        //close socket
    }
}
