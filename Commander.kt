//Commander is a bridge between World and ConnectionList it takes
//user commands and turns it into function calls and provides communication between
//the two objects and has the main loop
//All classes but the Listener are under here baby.
//I think this is ready to test

class Commander(howManyConnections: Int, welcome: String, bufferSize: Int, logger: Logger){

    val log = logger
    //var broadcastList: PuppetsInRoom
    //var newConnectionList: PuppetsInRoom
    //var disconnectedList: PuppetsInRoom
    //World object and ConnectionList object
    var connectionList: ConnectionList
    var world: World
    var tokenizer: Tokenizer
    var running: Boolean
    val MAX_CONNECTIONS: Int
    val WELCOME_MESSAGE: String
    val STRING_BUFFER_SIZE: Int

    init{
        MAX_CONNECTIONS = howManyConnections
        WELCOME_MESSAGE = welcome //add a member variable for this or remove
        STRING_BUFFER_SIZE = bufferSize
        connectionList = ConnectionList(howManyConnections, welcome, bufferSize, log)
        world = World(MAX_CONNECTIONS, log)
        tokenizer = Tokenizer()
        running = true
    }

    fun getConnectionNumber(puppetNumber: Int): Int{
        //Check if puppetListener is negative to add a helpful error message
        return world.puppetList.getPuppetListener(puppetNumber)
    }

//<called because of a command>
    fun broadcast(string: String, exclude: Int = -1){
        //use World.sendMsg to send string to everyone on the broadcast list excluding the exclude number
        //check that connectionNumber is in range and addMsg() returns true
        val bcList = connectionList.getBroadcastList()

        for(connectionNumber in bcList){
            if(connectionNumber != exclude){
                world.mudOutput.addMsg(connectionNumber, string)
            }
        }
    }

    fun chat(puppetNumber: Int, string: String){
        //Do I want to check for an empty string?
        var connectionNumber = getConnectionNumber(puppetNumber)
        var name = connectionList.getName(connectionNumber)

        world.mudOutput.addMsg(connectionNumber, "You: " + string)
        broadcast(name + ": " + string, connectionNumber)
    }

    fun quit(puppetNumber: Int, leftovers: String = ""){
        var connectionNumber = getConnectionNumber(puppetNumber)
        if(leftovers == ""){
            //If I remember, kill() adds the puppetNumber to the disconnectedList and puppet is removed
            //in checkDisconnectedList() below
            connectionList.kill(connectionNumber, "Goodbye")
        }
    }
//</called because of a command>

//<called during main loop>
    fun checkDisconnectedList(){
        //add a condition to clear the list only if the loop is actually entered
        val puppetNumberList = connectionList.getDisconnectedList()
        var clearList = false

        for(puppetNumber in puppetNumberList){
            world.removePlayer(puppetNumber)
            clearList = true
        }
        if(clearList){
            connectionList.clearDisconnetedList()
        }
    }

    fun checkNewConnectionList(){
        //add a condition to clear the list only if the loop is actually entered
        val newConnectionNumberList = connectionList.getNewConnectionList()
        var puppetNumber: Int
        var clearList = false

        for(connectionNumber in newConnectionNumberList){
            puppetNumber = world.insertNewPlayer(connectionNumber, connectionList.getName(connectionNumber))
            //setPuppetNumber() adds the puppetNumber to the broadcastList, if you change it for some other
            //reason make sure to remove the old number.
            connectionList.setPuppetNumber(connectionNumber, puppetNumber)
            clearList = true
        }
        if(clearList){
            connectionList.clearNewConnectionList()
        }
    }

    fun sendMudOutput(){
        connectionList.sendMudOutput(world.mudOutput)
    }

    fun getInput(){
        connectionList.getInput()
    }

    fun ExecuteCommand(){
        //Will I be taking one command at a time or all of them?
        //Commands: newroom, roomname, roomdescription, chat, newpuppet, say, look, quit
        var message: Msg?
        var puppetNumber: Int

        message = connectionList.mudInput.getMsg()
        if(message != null){
            puppetNumber = message.playerNumber
            tokenizer.passCommandString(message.messageString)
            val firstToken = tokenizer.getStringToken()
            when(firstToken){
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
                    //check if it is an exit
                    if(firstToken != ""){
                        val leftovers = tokenizer.getLeftovers()
                        if(leftovers == ""){
                            world.move(puppetNumber, firstToken, leftovers)
                        }
                        else{
                            world.mudOutput.addMsg(puppetNumber, "What?")
                        }
                    }
                }
            }
        }
    }

//</called during main loop>

    fun mainLoop(){
        checkDisconnectedList()
        checkNewConnectionList()
        sendMudOutput()
        getInput()
        ExecuteCommand()
        //check number of errors logged and flip running to false if greater than 0
    }

    fun shutdownMud(){
        connectionList.killAll("MUD server shutting down.")
        //Make sure puppets are saved and removed properly
        checkDisconnectedList()
    }

}