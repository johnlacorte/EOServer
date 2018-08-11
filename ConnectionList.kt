//Test this with any of the stuff for logging in with a name disabled
//I kinda want to use a PuppetsInRoom for numbers of new connections and lost connections
//I might want to rename it again so it makes more sense. Maybe IntegerList? IntegerSet?
//I keep thinking PuppetInRoom might either perform better or be easier to log errors if it had a boolean for if it holds any numbers
//but then I talk myself out of it as unnecessary.
//Commander should have function for broadcasts to make sure they come out of mudOutput queue after anything already in there
//I think I want a dictionary for usernames and passwords here
//needs to be ready for multithreading
//I might want to consider adding the puppetNumber to disconnectedList instead of connectionNumber
//perhaps adding a function to look it up and add it at the same time
//dealing with lost connections should be done before new connections to prevent a new connection being killed
//because its number is on the disconnect list still

import java.net.*
import java.io.*

class ConnectionList(howManyConnections: Int, welcome: String, bufferSize: Int, logger: Logger){
    //Constructor should take string buffer size and welcome message too
    var connectionArray: Array<Connection>
    val MAX_CONNECTIONS: Int
    val WELCOME_MESSAGE: String
    val STRING_BUFFER_SIZE: Int
    val log = logger
    var mudInput: MsgQueue
    var broadcastList: PuppetsInRoom
    var newConnectionList: PuppetsInRoom
    var disconnectedList: PuppetsInRoom

    init{
        MAX_CONNECTIONS = howManyConnections
        WELCOME_MESSAGE = welcome
        STRING_BUFFER_SIZE = bufferSize
        conectionArray = Array(MAX_CONNECTIONS, { i -> Connection(i, STRING_BUFFER_SIZE)})
        mudInput = MsgQueue(log)
        broadcastList = PuppetsInRoom()
        newConnectionList = PuppetsInRoom()
        disconnectedList = PuppetsInRoom()
    }

    fun isConnectionNumberInRange(connectionNumber): Boolean{
        //Used in functions below that take a connection number to check if connection number is in the expected range
        var success: Boolean

        if(connectionNumber >= 0 && connectionNumber < MAX_CONNECTIONS){
            success = true
        }
        else{
            success = false
        }
        return success
    }

    fun lostConnection(connectionNumber): Boolean{
        //This function is called if a client disconnects unexpectedly and when the server breaks
        //the connection after sending a message
        var success = false
        var puppetNumber = -1

        if(isConnectionNumberInRange(connectionNumber)){
            if(conectionArray[connectionNumber].clientConnected){
                //If the connection has a puppet in the MUD, add puppet number to the disconnectedList
                //and the puppet will be killed when the main loop starts again
                puppetNumber = connectionArray[connectionNumber].clientPuppetNumber
                if(puppetNumber != -1){
                    broadcastList.removePuppet(connectionNumber)
                    disconnectedList.addPuppet(puppetNumber)
                    setPuppetNumber(connectionNumber, -1)
                }
                connectionArray[connectionNumber].clientConnected = false
                connectionArray[connectionNumber].clientSocket.close()
            }
            success = true
        }
        else{
            log.logError("Connection number out of range: \n  lostConnection(" + connectionNumber.toString() + ")")
        }
        return success
    }

    fun newConnection(socket: Socket): Boolean{
        //return success boolean
        //send welcome and login message
        var success = false
        var connectionNumber = -1
        var outputWriter = OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1")

        for(connection in connectionArray){
            if(!connection.clientConnected){
                connectionNumber = connection.clientNumber
                break
            }
        }
        if(connectionNumber != -1){
            try{
                outputWriter.write(WELCOME_MESSAGE + "\n" + "\nWhat is your name?\n")
                success = true
            }
            catch(IOException e){
                socket.close()
                //Log this as info and any false return values of the stuff below as errors
                //Do I need to do something with outputWriter?
            }
            if(success){
                connectionArray[connectionNumber].clientConnected = true
                connectionArray[connectionNumber].clientSocket = socket
                connectionArray[connectionNumber].clientName = "ClientName"
                connectionArray[connectionNumber].clientPuppetNumber = -1
                connectionArray[connectionNumber].inputString = StringBuffer(STRING_BUFFER_SIZE)
                //add to new connection list, move this to getInput()
                newConnectionList.addPuppet(connectionNumber)
                //log new connect, move this to getInput()
            }
        }
        //maybe log too many connections?
        else{
            outputWriter.write("Server is at maximum number of connections.")
            socket.close()
        }
        return success
    }

    fun sendMsg(msg: Msg): Boolean{
        //word wrap here maybe
        //may need a flush()
        //maybe kill connection from in here
        //check if connection number is in range and if clientConnected and maybe if socket is closed
        var success = false
        var connectionNumber = msg.playerNumber
        var outputWriter: OutputStreamWriter

        if(isConnectionNumberInRange(connectionNumber){
            outputWriter = OutputStreamWriter(conectionArray[connectionNumber].clientSocket.getOutputStream(), "ISO-8859-1")
            try{
                outputWriter.write(msg.messageString + "\n")
                success = true
            }
            catch(IOException e){
                lostConnection(connectionNumber)
                //Log this as info and any false return values of the stuff below as errors
            }
        }
        else{
            log.logError("Connection number out of range: \n  sendMsg( Msg(" + connectionNumber.toString() + ", " + msg.messageString + ") )")
        }
        return success
    }

    fun kill(connectionNumber: Int, message: String): Boolean{
        //I don't know if I should check the return value of sendMsg() because if it fails it
        //adds the number to disconnectedList to be killed in puppetList and I don't know, maybe
        //check that the connectionNumber is in range and the status of the connection before
        //attempting to send message
        var success = false
        var puppetNumber = -1

        if(isConnectionNumberInRange(connectionNumber)){
            //I don't know if I need to check this if sendMsg checks it as well
            if(connectionArray[connectionNumber].clientConnected){
                //sendMsg() will call lostConnection() before returning false
                if( sendMsg( Msg(connectionNumber, message) ) ){
                    lostConnection(connectNumber)
                }
            }
            //else maybe log an error, tried to kill disconnected connection, maybe just during
            //testing because a connection could be lost before this is called
            success = true
        }
        else{
            log.logError("Connection number out of range: \n  kill(" + connectionNumber.toString() + ", " + message + ")")
        }
        return success
    }

    fun getInput(){
        //I might want to actually read stuff into buffer with another function
        //Do I want to return a value if theres input?
        //Do I need to check is socket is open?
        //If it all works add another branch for getting player name and password
        var connectionNumber = -1
        var readInt: Int
        var readChar: Char
        var outputWriter: OutputStreamWriter
        var inputReader: InputStreamReader

        for(connection in connectionArray){
            if(connection.clientConnected){
                connectionNumber = connection.clientNumber
                //get any input
                outputWriter = OutputStreamWriter(connection.clientSocket.getOutputStream(), "ISO-8859-1")
                inputReader = InputStreamReader(connection.clientSocket.getInputStream())
                while(inputReader.ready()){
                    readInt = inputReader.read()
                    if(readInt == -1){
                        //I don't actually know if ready() will come back as true if the client unexpectedly disconnects
                        //this is here just in case
                        lostConnection(connectionNumber)
                        //Log this as info and any false return values of the stuff below as errors
                        break
                    }
                    else{
                        //check for too many characters in buffer or newline
                        readChar = readInt.toChar()
                        if(readChar == '\n'){
                            //if(connection.clientPuppetNumber == -1){
                                //login stuff. change name and add to whatever new connection are stored in
                            //    connection.clientName = connection.inputString.toString()
                            //}
                            //else{
                                //add msg to msgqueue
                                mudInput.addMsg(connection.clientPuppetNumber, connection.inputString.toString())
                            //}
                            connection.inputString = StringBuffer(STRING_BUFFER_SIZE)
                        }
                        else{
                            connection.inputString.append(readChar)
                            if(connection.inputString.length == STRING_BUFFER_SIZE){
                                //break connection if line is too long
                                connection.clientConnected = false
                                connection.clientSocket.close()
                                //Log this as info and any false return values of the stuff below as errors
                                broadcastList.removePuppet(connectionNumber)
                                disconnectedList.addPuppet(connectionNumber)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    fun setPuppetNumber(connectionNumber: Int, puppetNumber: Int): Boolean{
        var success = false

        if(isConnectionNumberInRange(connectionNumber)){
            success = connectionArray[connectionNumber].clientConnected
            if(success){
                //check this is still connected and log an error if not. might not be caused by an error
                //but it could be  want to know for sure in case it is.
                connectionArray[connectionNumber].clientPuppetNumber = puppetNumber
                //check if addPuppet returns false and log an error
                success = broadcastList.addPuppet(connectNumber)

            }
        }
        //else{connectNumber out of range}
        return success
    }

    fun getName(connectionNumber: Int): String{
        //check if still connected
        var retString = ""

        if(isConnectionNumberInRange(connectionNumber)){
            retString = connectionArray[connectionNumber].clientName
        }
        return retString
    }

    fun getConnectionList() List<Int>{
        return broadcastList.puppetNumbers()
    }

    fun getNewConnectionList() List<Int>{
        return newConnectionList.puppetNumbers()
    }

    fun clearNewConnectionList(){
        newConnectionList = PuppetsInRoom()
    }

    fun getDisconnectedList() List<Int>{
        return disconnectedList.puppetNumbers()
    }

    fun clearDisconnetedList(){
        disconnectedList = PuppetsInRoom()
    }
}
