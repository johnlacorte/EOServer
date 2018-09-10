//Commander is a bridge between World and ConnectionList it takes
//user commands and turns it into function calls and provides communication between
//the two objects and has the main loop

/**
 * The Commander has the main loop of the MUD and the switch block for all the commands to call the function that carries it out.
 *
 * @param MAX_CONNECTIONS The maximum number of connections to the MUD.
 * @param WELCOME_MESSAGE The message to send on a new connection.
 * @param STRING_BUFFER_SIZE The maximum length of a command.
 * @param log The Logger object for logging errors and info.
 * @property connectionList A [ConnectionList].
 * @property world A [World] object.
 * @property tokenizer A [Tokenizer].
 * @property running A boolean for if the main loop should be executed again.
 */
class Commander(val MAX_CONNECTIONS: Int, val WELCOME_MESSAGE: String, val STRING_BUFFER_SIZE: Int, var log: Logger){

    var connectionList: ConnectionList
    var world: World
    var tokenizer: Tokenizer
    var running: Boolean

    init{
        connectionList = ConnectionList(MAX_CONNECTIONS, WELCOME_MESSAGE, STRING_BUFFER_SIZE, log)
        world = World(MAX_CONNECTIONS, log)
        tokenizer = Tokenizer()
        running = true
    }

    /**
     * Gets the listener (connectionNumber) of a [Puppet].
     *
     * @param puppetNumber The number of the [Puppet].
     * @returns The listener (connectionNumber) of a [Puppet].
     */
    fun getConnectionNumber(puppetNumber: Int): Int{
        //Check if puppetListener is negative to add a helpful error message
        return world.puppetList.getPuppetListener(puppetNumber)
    }

//<called because of a command>
    /**
     * This is used to send a message to everyone on the broadcastList.
     *
     * @param string The message to be sent.
     * @param exclude (Optional) a connection number can be excluded.
     */
    fun broadcast(string: String, exclude: Int = -1){
        //check that connectionNumber is in range and addMsg() returns true
        val bcList = connectionList.getBroadcastList()

        for (connectionNumber in bcList){
            if (connectionNumber != exclude){
                world.mudOutput.addMsg(connectionNumber, string)
            }
        }
    }

    /**
     * Uses broadcast to send a message from one player to all the others.
     *
     * @param puppetNumber The puppet number of the player to send the chat message.
     * @param string The chat message to send.
     */
    fun chat(puppetNumber: Int, string: String){
        //Do I want to check for an empty string?
        var connectionNumber = getConnectionNumber(puppetNumber)
        var name = connectionList.getName(connectionNumber)

        world.mudOutput.addMsg(connectionNumber, "You: " + string)
        broadcast(name + ": " + string, connectionNumber)
    }

    /**
     * Lets a player leave the game.
     *
     * @param puppetNumber The puppet number of the player.
     * @param leftovers This is any other arguments after "quit". This will give a message if anything other than an empty string.
     */
    fun quit(puppetNumber: Int, leftovers: String = ""){
        var connectionNumber = getConnectionNumber(puppetNumber)
        if (leftovers == ""){
            //If I remember, kill() adds the puppetNumber to the disconnectedList and puppet is removed
            //in checkDisconnectedList() below
            connectionList.kill(connectionNumber, "Goodbye")
        }
    }
//</called because of a command>

//<called during main loop>
    /**
     * First step in the main loop. Remove any puppets in disconnectedList from world.puppetList.
     */
    fun checkDisconnectedList(){
        //add a condition to clear the list only if the loop is actually entered
        val puppetNumberList = connectionList.getDisconnectedList()
        var clearList = false

        for (puppetNumber in puppetNumberList){
            world.removePlayer(puppetNumber)
            clearList = true
        }
        if (clearList){
            connectionList.clearDisconnectedList()
        }
    }

    /**
     * Second step in the main loop. Create new puppets for any connection numbers found in newConnectionList.
     *
     * Connection numbers are not added to newConnectionList until they have entered a name and are ready to have a
     * [Puppet] inserted into the MUD.
     */
    fun checkNewConnectionList(){
        val newConnectionNumberList = connectionList.getNewConnectionList()
        var puppetNumber: Int
        var clearList = false

        for (connectionNumber in newConnectionNumberList){
            puppetNumber = world.insertNewPlayer(connectionNumber, connectionList.getName(connectionNumber))
            //setPuppetNumber() adds the puppetNumber to the broadcastList, if you change it for some other
            //reason make sure to remove the old number.
            connectionList.setPuppetNumber(connectionNumber, puppetNumber)
            clearList = true
        }
        if (clearList){
            connectionList.clearNewConnectionList()
        }
    }

    /**
     * Third step in the main loop. Send any messages in mudOutput [MsgQueue].
     */
    fun sendMudOutput(){
        connectionList.sendMudOutput(world.mudOutput)
    }

    /**
     * Step four, get any characters from streams of connected sockets. This will add any full lines to mudInput [MsgQueue].
     */
    fun getInput(){
        connectionList.getInput()
    }

    /**
     * Step five, take one command from mudInput, break it down to individual parts, and call any functions.
     */
    fun ExecuteCommand(){
        //Will I be taking one command at a time or all of them?
        //Commands: newroom, roomname, roomdescription, chat, newpuppet, say, look, quit
        var message: Msg?
        var puppetNumber: Int

        message = connectionList.mudInput.getMsg()
        if (message != null){
            puppetNumber = message.number
            tokenizer.passCommandString(message.message)
            val firstToken = tokenizer.getStringToken()
            when (firstToken){
                "newroom" -> {
                    val newRoomDirection = tokenizer.getStringToken()
                    val directionBackHere = tokenizer.getStringToken()
                    val leftovers = tokenizer.getLeftovers()
                    world.makeRoom(puppetNumber, newRoomDirection, directionBackHere, leftovers)
                }
                "roomname" -> {
                    val newName = tokenizer.getLeftovers()
                    world.changeRoomName(puppetNumber, newName)
                }
                "roomdescription" -> {
                    val newDescription = tokenizer.getLeftovers()
                    world.changeRoomDescription(puppetNumber, newDescription)
                }
                "chat" -> {
                    val chatMessage = tokenizer.getLeftovers()
                    chat(puppetNumber, chatMessage)
                }
                "newpuppet" -> {
                    val newPuppetName = tokenizer.getLeftovers()
                    world.newPuppet(puppetNumber, newPuppetName)
                }
                "say" -> {
                    val sayMessage = tokenizer.getLeftovers()
                    world.say(puppetNumber, sayMessage)
                }
                "look" -> {
                    val leftovers = tokenizer.getLeftovers()
                    world.lookAround(puppetNumber, leftovers)
                }
                "quit" -> {
                    val leftovers = tokenizer.getLeftovers()
                    quit(puppetNumber, leftovers)
                }
                "shutdown" -> {
                    running = false
                }
                else -> {
                    //Do nothing if it is an empty string.
                    if (firstToken != ""){
                        val leftovers = tokenizer.getLeftovers()
                        //check if it is an exit with world.isThisThingAnExit()
                        if (leftovers == "" && world.isThisThingAnExit(puppetNumber, firstToken)){
                            world.move(puppetNumber, firstToken)
                        }
                        else {
                            world.mudOutput.addMsg(puppetNumber, "What?")
                        }
                    }
                }
            }
        }
    }

//</called during main loop>

    /**
     * This is the main loop of the MUD.
     *
     * I will probably add a check for if any errors were logged since the last time as the sixth step.
     */
    fun mainLoop(){
        checkDisconnectedList()
        checkNewConnectionList()
        sendMudOutput()
        getInput()
        ExecuteCommand()
        //check number of errors logged and flip running to false if greater than 0
    }

    /**
     * This needs to be called before the MUD is shutdown so any player data that needs to be saved will be.
     */
    fun shutdownMud(){
        connectionList.killAll("MUD server shutting down.")
        //Make sure puppets are saved and removed properly
        checkDisconnectedList()
    }

}