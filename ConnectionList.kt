//Test this with any of the stuff for logging in with a name disabled
//I kinda want to use a PuppetsInRoom for numbers of new connections and lost connections
//I might want to rename it again so it makes more sense. Maybe IntegerList? IntegerSet?
//I keep thinking PuppetInRoom might either perform better or be easier to log errors if it had a boolean for if it holds any numbers
//but then I talk myself out of it as unnecessary.
//Commander should have function for broadcasts to make sure they come out of mudOutput queue after anything already in there
//I think I want a dictionary for usernames and passwords here
//needs to be ready for multithreading
//dealing with lost connections should be done before new connections to prevent a new connection being killed
//because its number is on the disconnect list still
//Log info for connection stuff and maybe find something else to log for an error
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
        connectionArray = Array(MAX_CONNECTIONS, { i -> Connection(i, STRING_BUFFER_SIZE)})
        mudInput = MsgQueue(log)
        broadcastList = PuppetsInRoom()
        newConnectionList = PuppetsInRoom()
        disconnectedList = PuppetsInRoom()
    }

    @Synchronized fun isConnectionNumberInRange(connectionNumber: Int): Boolean{
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

    @Synchronized fun lostConnection(connectionNumber: Int): Boolean{
        //This function is called if a client disconnects unexpectedly and when the server breaks
        //the connection after sending a message
        var success = false
        var puppetNumber: Int

        if(isConnectionNumberInRange(connectionNumber)){
            if(connectionArray[connectionNumber].clientConnected){
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

    @Synchronized fun newConnection(socket: Socket): Boolean{
        //return success boolean
        //send welcome and login message
        //log info message about new connect
        var success = false
        var connectionNumber = -1
        var outputWriter = OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1")

        print("newConnection()\n")
        for(connection in connectionArray){
            if(!connection.clientConnected){
                connectionNumber = connection.clientNumber
                break
            }
        }
        //see if welcome message is sent without checking connectionNumber
        if(connectionNumber != -1){
            try{
                outputWriter.write(WELCOME_MESSAGE + "\n" + "\nWhat is your name?\n")
                outputWriter.flush()
                success = true
            }
            catch(e: IOException){
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
                //newConnectionList.addPuppet(connectionNumber)
                //log new connect, move this to getInput()
            }
        }
        //maybe log too many connections?
        else{
            outputWriter.write("Server is at maximum number of connections.")
            outputWriter.flush()
            socket.close()
        }
        return success
    }

    @Synchronized fun sendMsg(msg: Msg): Boolean{
        //word wrap here maybe
        //may need a flush()
        //maybe kill connection from in here
        //check if connection number is in range and if clientConnected and maybe if socket is closed
        var success = false
        var connectionNumber = msg.playerNumber
        var outputWriter: OutputStreamWriter

        if(isConnectionNumberInRange(connectionNumber)){
            outputWriter = OutputStreamWriter(connectionArray[connectionNumber].clientSocket.getOutputStream(), "ISO-8859-1")
            try{
                outputWriter.write(msg.messageString + "\n")
                outputWriter.flush()
                success = true
            }
            //This seems too simple to be right
            catch(e: IOException){
                lostConnection(connectionNumber)
                //Log this as info and any false return values of the stuff below as errors
            }
        }
        else{
            log.logError("Connection number out of range: \n  sendMsg( Msg(" + connectionNumber.toString() + ", " + msg.messageString + ") )")
        }
        return success
    }

    @Synchronized fun sendMudOutput(output: MsgQueue): Boolean{
        var success = true
        var message: Msg?

        message = output.getMsg()
        while(message != null){
            if(!sendMsg(message)){
                success = false
            }
            message = output.getMsg()
        }
        return success
    }

    @Synchronized fun kill(connectionNumber: Int, message: String): Boolean{

        var success = false

        if(isConnectionNumberInRange(connectionNumber)){
            //I don't know if I need to check this if sendMsg checks it as well
            if(connectionArray[connectionNumber].clientConnected){
                //sendMsg() will call lostConnection() if it fails before returning false
                if( sendMsg( Msg(connectionNumber, message) ) ){
                    lostConnection(connectionNumber)
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

    @Synchronized fun killAll(message: String){
        //Do I want to close the socket if !clientConnected anyway?
        var connectionNumber: Int

        for(connection in connectionArray){
            connectionNumber = connection.clientNumber
            if(connection.clientConnected){
                //sendMsg() will call lostConnection() if it fails before returning false
                if( sendMsg( Msg(connectionNumber, message) ) ){
                    lostConnection(connectionNumber)
                }
            }
        }
    }

    @Synchronized fun getInput(){
        //I might want to actually read stuff into buffer with another function
        //Do I want to return a value if theres input?
        //Do I need to check is socket is open?
        //If it all works add another branch for getting player name and password
        var connectionNumber: Int
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
                            if(connection.clientPuppetNumber == -1){
                                //login stuff. change name and add to whatever new connection are stored in
                                connection.clientName = connection.inputString.toString()
                                //log to info instead of printing please
                                print("Name = " + connection.clientName.toString() + "\n")
                                if(!newConnectionList.addPuppet(connectionNumber)){
                                    log.logError("Failed to add to newConnectionList: \n  getInput()")
                                }
                            }
                            else{
                                //add msg to msgqueue
                                mudInput.addMsg(connection.clientPuppetNumber, connection.inputString.toString())
                            }
                            connection.inputString = StringBuffer(STRING_BUFFER_SIZE)
                        }
                        else{
                            //hopefully this will ignore any unexpected control characters
                            if(readInt > 31){
                                connection.inputString.append(readChar)
                            }
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

    @Synchronized fun setPuppetNumber(connectionNumber: Int, puppetNumber: Int): Boolean{
        //This is meant to assign a new puppet number to a newly connected client
        //If changing the puppet number for another reason keep in mind the old puppet number
        //will be left in the broadcast list
        var success = false

        if(isConnectionNumberInRange(connectionNumber)){
            success = connectionArray[connectionNumber].clientConnected
            if(success){
                //check this is still connected and log an error if not. might not be caused by an error
                //but it could be  want to know for sure in case it is.
                connectionArray[connectionNumber].clientPuppetNumber = puppetNumber
                //check if addPuppet returns false and log an error
                if(!broadcastList.addPuppet(connectionNumber)){
                    log.logError("Failed to add to broadcastList: \n  setPuppetNumber(" + connectionNumber.toString() + ", " + puppetNumber.toString() + ")")
                }
            }
            else{
                log.logError("Connection clientConnected is false: \n  setPuppetNumber(" + connectionNumber.toString() + ", " + puppetNumber.toString() + ")")
            }
        }
        else{
            log.logError("Connection number out of range: \n  setPuppetNumber(" + connectionNumber.toString() + ", " + puppetNumber.toString() + ")")
        }
        return success
    }

    @Synchronized fun getName(connectionNumber: Int): String{
        //check if still connected
        var retString = ""

        if(isConnectionNumberInRange(connectionNumber)){
            retString = connectionArray[connectionNumber].clientName
        }
        else{
            log.logError("Connection number out of range: \n  getName(" + connectionNumber.toString() + ")")
        }
        return retString
    }

    @Synchronized fun getBroadcastList(): List<Int>{
        return broadcastList.puppetNumbers()
    }

    @Synchronized fun getNewConnectionList(): List<Int>{
        return newConnectionList.puppetNumbers()
    }

    @Synchronized fun clearNewConnectionList(){
        newConnectionList = PuppetsInRoom()
    }

    @Synchronized fun getDisconnectedList(): List<Int>{
        return disconnectedList.puppetNumbers()
    }

    @Synchronized fun clearDisconnetedList(){
        disconnectedList = PuppetsInRoom()
    }
}
