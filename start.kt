
fun main(args: Array<String>) {
    val portNumber = args[0].toInt()
    var logger = Logger()
    var welcome = "Welcome to test15!"
    var commander = Commander(10, welcome, 2000, logger)
    var listenerThread = Thread(Listener(commander.connectionList, portNumber))

    listenerThread.start()
    while(commander.running){
        commander.mainLoop()
    }

    commander.shutdownMud()
    System.exit(0)
}