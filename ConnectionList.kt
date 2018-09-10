//I think I want a dictionary for usernames and passwords here
//dealing with lost connections should be done before new connections to prevent a new connection being killed
//because its number is on the disconnect list still
//Log info for connection stuff and maybe find something else to log for an error
import java.net.*
import java.io.*

/**
 * This is a collection of [Connection] objects and the functions to use them.
 *
 * @param MAX_CONNECTIONS The maximum number of connections allowed before new connections are turned away.
 * @param WELCOME_MESSAGE This is the welcome message for new connections.
 * @param STRING_BUFFER_SIZE This is the maximum number of characters that can be in one line of input.
 * @param log The [Logger] for error messages and info messages.
 */
class ConnectionList(val MAX_CONNECTIONS: Int, val WELCOME_MESSAGE: String, val STRING_BUFFER_SIZE: Int, var log: Logger){
    var connectionArray: Array<Connection>
    var mudInput: MsgQueue
    var broadcastList: MutableSet<Int>
    var newConnectionList: MutableSet<Int>
    var disconnectedList: MutableSet<Int>

    init{
        connectionArray = Array(MAX_CONNECTIONS, { i -> Connection(i, STRING_BUFFER_SIZE)})
        mudInput = MsgQueue(log)
        broadcastList = mutableSetOf()
        newConnectionList = mutableSetOf()
        disconnectedList = mutableSetOf()
    }

    /**
     * Checks a connection number.
     *
     * @param connectionNumber Number to check.
     * @returns True or false.
     */
    @Synchronized fun isConnectionNumberInRange(connectionNumber: Int): Boolean{
        var success: Boolean

        if (connectionNumber >= 0 && connectionNumber < MAX_CONNECTIONS){
            success = true
        }
        else {
            success = false
        }
        return success
    }

    /**
     * This function is called if it is detected that a client has disconnected.
     *
     * This also is called when a connection is ended by the server because the last steps of doing that are
     * the same as the steps in lostConnection(). This also removes connectionNumber from the broadcastList
     * and adds the puppetNumber to disconnectedList so that the Puppet can be killed too.
     *
     * @param connectionNumber The connection number to close.
     * @returns Success.
     */
    @Synchronized fun lostConnection(connectionNumber: Int): Boolean{
        var success = false
        var puppetNumber: Int

        if (isConnectionNumberInRange(connectionNumber)){
            if (connectionArray[connectionNumber].clientConnected){
                //If the connection has a puppet in the MUD, add puppet number to the disconnectedList
                //and the puppet will be killed when the main loop starts again
                puppetNumber = connectionArray[connectionNumber].clientPuppetNumber
                if (puppetNumber != -1){
                    broadcastList.remove(connectionNumber)
                    disconnectedList.add(puppetNumber)
                    setPuppetNumber(connectionNumber, -1)
                }
                connectionArray[connectionNumber].clientConnected = false
                connectionArray[connectionNumber].clientSocket.close()
            }
            success = true
        }
        else {
            log.logError("Connection number out of range: \n  ConnectionList.lostConnection(${connectionNumber.toString()})")
        }
        return success
    }

    /**
     * This is how the listener thread adds a new socket to ConnectionList.
     *
     * This send welcome message and prompt to log in as well. I probably will add logging the new connections info later
     * maybe during my next round of additions.
     *
     * @param socket A Socket object for a new connection.
     * @Returns Success.
     */
    @Synchronized fun newConnection(socket: Socket): Boolean{
        //log info message about new connect
        var success = false
        var connectionNumber = -1
        var outputWriter = OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1")

        for (connection in connectionArray){
            if (!connection.clientConnected){
                connectionNumber = connection.clientNumber
                break
            }
        }
        //see if welcome message is sent without checking connectionNumber
        if (connectionNumber != -1){
            try {
                outputWriter.write("$WELCOME_MESSAGE\n\nWhat is your name?\n")
                outputWriter.flush()
                success = true
            }
            catch (e: IOException){
                socket.close()
                //Log this as info and any false return values of the stuff below as errors
                //Do I need to do something with outputWriter?
            }
            if (success){
                connectionArray[connectionNumber].clientConnected = true
                connectionArray[connectionNumber].clientSocket = socket
                connectionArray[connectionNumber].clientName = "ClientName"
                connectionArray[connectionNumber].clientPuppetNumber = -1
                connectionArray[connectionNumber].inputString = StringBuffer(STRING_BUFFER_SIZE)
            }
        }
        //maybe log too many connections?
        else {
            outputWriter.write("Server is at maximum number of connections.")
            outputWriter.flush()
            socket.close()
        }
        return success
    }

    /**
     * This sends a single [Msg] usually from mudOutput.
     *
     * This is where it is most likely to detect an unexpected disconnection. I am considering adding the code for
     * word wrap either here or at the point where the Msg object is constructed.
     *
     * @param msg Msg to send.
     * @returns success.
     */
    @Synchronized fun sendMsg(msg: Msg): Boolean{
        //check if connection number is in range and if clientConnected and maybe if socket is closed
        var success = false
        var connectionNumber = msg.number
        var outputWriter: OutputStreamWriter

        if (isConnectionNumberInRange(connectionNumber)){
            outputWriter = OutputStreamWriter(connectionArray[connectionNumber].clientSocket.getOutputStream(), "ISO-8859-1")
            try {
                outputWriter.write(msg.message + "\n")
                outputWriter.flush()
                success = true
            }
            //This seems too simple to be right
            catch (e: IOException){
                lostConnection(connectionNumber)
                //Log this as info and any false return values of the stuff below as errors
            }
        }
        else {
            log.logError("Connection number out of range: \n  ConnectionList.sendMsg( Msg(${connectionNumber.toString()}, $msg.message) )")
        }
        return success
    }

    /**
     * I had originally planned on having the Commander class have a loop do this but I thought putting it here
     * would be nicer because I can keep it with related stuff.
     *
     * @param output This is a MsgQueue. Usually going to be mudOutput.
     * @returns Success.
     */
    @Synchronized fun sendMudOutput(output: MsgQueue): Boolean{
        var success = true
        var message: Msg?

        message = output.getMsg()
        while (message != null){
            if (!sendMsg(message)){
                success = false
            }
            message = output.getMsg()
        }
        return success
    }

    /**
     * Sends a message and calls lostConnection to close connection.
     *
     * @param connectionNumber Number of the connection.
     * @param message String. Message to be sent before disconnecting.
     * @returns Success.
     */
    @Synchronized fun kill(connectionNumber: Int, message: String): Boolean{
        var success = false

        if (isConnectionNumberInRange(connectionNumber)){
            //I don't know if I need to check this if sendMsg checks it as well
            if (connectionArray[connectionNumber].clientConnected){
                //sendMsg() will call lostConnection() if it fails before returning false
                if ( sendMsg( Msg(connectionNumber, message) ) ){
                    lostConnection(connectionNumber)
                }
            }
            //else maybe log an error, tried to kill disconnected connection, maybe just during
            //testing because a connection could be lost before this is called
            success = true
        }
        else {
            log.logError("Connection number out of range: \n  ConnectionList.kill(${connectionNumber.toString()}, $message)")
        }
        return success
    }

    /**
     * Kills all connections and adds their puppetNumbers to disconnectedList for their Puppets to be removed as well.
     *
     * @param message String. Message to be sent beofre disconnecting.
     */
    @Synchronized fun killAll(message: String){
        //Do I want to close the socket if !clientConnected anyway?
        //Do I need to return success?
        var connectionNumber: Int

        for (connection in connectionArray){
            connectionNumber = connection.clientNumber
            if (connection.clientConnected){
                //sendMsg() will call lostConnection() if it fails before returning false
                if (sendMsg( Msg(connectionNumber, message))){
                    lostConnection(connectionNumber)
                }
            }
        }
    }

    /**
     * Checks if any input is available on any streams and adds it to mudInput MsgQueue.
     *
     * This does a lot of things. It adds characters to a string buffer ignoring unexpected control characters.
     * It continues until either it runs out of characters from the streams, a newline is found, or string buffer
     * is full. If string buffer gets full, it sends a message and disconnects that connection. If the connection
     * receiving input hasn't been assigned a puppetNumber yet, it will assume the string grabbed from the string
     * buffer is the players name. Otherwise, This will construct a Msg with the puppetNumber of the
     * connection and the string from the string buffer to create a command to store in mudInput MsgQueue.
     */
    @Synchronized fun getInput(){
        //Do I want to return a value if theres input?
        //Do I need to check is socket is open?
        //If it all works add another branch for getting player name and password
        var connectionNumber: Int
        var readInt: Int
        var readChar: Char
        //var outputWriter: OutputStreamWriter
        var inputReader: InputStreamReader

        for (connection in connectionArray){
            if (connection.clientConnected){
                connectionNumber = connection.clientNumber
                //get any input
                //outputWriter = OutputStreamWriter(connection.clientSocket.getOutputStream(), "ISO-8859-1")
                inputReader = InputStreamReader(connection.clientSocket.getInputStream())
                while (inputReader.ready()){
                    readInt = inputReader.read()
                    if (readInt == -1){
                        //I don't actually know if ready() will come back as true if the client unexpectedly disconnects
                        //this is here just in case
                        lostConnection(connectionNumber)
                        //Log this as info and any false return values of the stuff below as errors
                        break
                    }
                    else {
                        //check for too many characters in buffer or newline
                        readChar = readInt.toChar()
                        if (readChar == '\n'){
                            if (connection.clientPuppetNumber == -1){
                                //login stuff. change name and add to whatever new connection are stored in
                                connection.clientName = connection.inputString.toString()
                                //log to info instead of printing please
                                print("Name = " + connection.clientName.toString() + "\n")
                                if (!newConnectionList.add(connectionNumber)){
                                    log.logError("Failed to add to newConnectionList: \n  getInput()")
                                }
                            }
                            else {
                                //add msg to msgqueue
                                mudInput.addMsg(connection.clientPuppetNumber, connection.inputString.toString())
                            }
                            connection.inputString = StringBuffer(STRING_BUFFER_SIZE)
                        }
                        else {
                            //hopefully this will ignore any unexpected control characters
                            if (readInt > 31){
                                connection.inputString.append(readChar)
                            }
                            if (connection.inputString.length == STRING_BUFFER_SIZE){
                                //break connection if line is too long
                                connection.clientConnected = false
                                connection.clientSocket.close()
                                //Log this as info and any false return values of the stuff below as errors
                                broadcastList.remove(connectionNumber)
                                disconnectedList.add(connectionNumber)
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the puppetNumber of a connection object.
     *
     * Probably only want to use this for new connections and disconnections only. It might introduce
     * bugs trying to do anything else. This does nothing to change numbers in the broadcastList.
     *
     * @param connectionNumber The number of the connection.
     * @param puppetNumber The number to set it to.
     * @returns Success.
     */
    @Synchronized fun setPuppetNumber(connectionNumber: Int, puppetNumber: Int): Boolean{
        var success = false

        if (isConnectionNumberInRange(connectionNumber)){
            success = connectionArray[connectionNumber].clientConnected
            if (success){
                //check this is still connected and log an error if not. might not be caused by an error
                //but it could be  want to know for sure in case it is.
                connectionArray[connectionNumber].clientPuppetNumber = puppetNumber
                //check if addPuppet returns false and log an error
                if (!broadcastList.add(connectionNumber)){
                    log.logError("Failed to add to broadcastList: \n  ConnectionList.setPuppetNumber(${connectionNumber.toString()}, ${puppetNumber.toString()})")
                }
            }
            else {
                log.logError("Connection clientConnected is false: \n  ConnectionList.setPuppetNumber(${connectionNumber.toString()}, ${puppetNumber.toString()})")
            }
        }
        else {
            log.logError("Connection number out of range: \n  ConnectionList.setPuppetNumber(${connectionNumber.toString()}, ${puppetNumber.toString()})")
        }
        return success
    }

    /**
     * Gets the name that was assigned to a connection.
     *
     * @param connectionNumber ConnectionNumber.
     * @returns String. The name or an empty string.
     */
    @Synchronized fun getName(connectionNumber: Int): String{
        var retString = ""

        if (isConnectionNumberInRange(connectionNumber)){
            retString = connectionArray[connectionNumber].clientName
        }
        else {
            log.logError("Connection number out of range: \n  ConnectionList.getName(${connectionNumber.toString()})")
        }
        return retString
    }

    /**
     * Gets a list of integers from the broadcastList.
     *
     * @returns A list of integers.
     */
    @Synchronized fun getBroadcastList(): List<Int>{
        return broadcastList.toList()
    }

    /**
     * Gets a list of integers from the newConnectionList.
     *
     * @returns A list of integers.
     */
    @Synchronized fun getNewConnectionList(): List<Int>{
        return newConnectionList.toList()
    }

    /**
     * Clears the new collection list
     */
    @Synchronized fun clearNewConnectionList(){
        newConnectionList.clear()
    }

    /**
     * Gets a list of integers from disconnectedList.
     */
    @Synchronized fun getDisconnectedList(): List<Int>{
        return disconnectedList.toList()
    }

    /**
     * Clears the disconnectedList.
     */
    @Synchronized fun clearDisconnectedList(){
        disconnectedList.clear()
    }
}
