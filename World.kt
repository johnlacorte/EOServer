//World is a bridge between rooms contained in a RoomContainer and puppets
//This class will change as the vision and features change
//Functions for adding multiple messages for players 
//This should take an argument to decide to start with an empty world or
//load files
//import java.util.*
//Constructors are different now
//change puppetList to playerPuppetList

class World(howManyConnections: Int){
    //I need functions for everything you can do
    var MAX_CONNECTIONS: Int
    var roomList: RoomList
    var puppetList: PuppetList
    var mudOutput: MsgQueue
    val log: Logger

    init{
        MAX_CONNECTIONS = howManyConnections
        roomList = RoomList(MAX_CONNECTIONS)
        puppetList = PuppetList(MAX_CONNECTIONS)
        mudOutput = MsgQueue(MAX_CONNECTIONS)
        log = Logger()
    }

    fun msgTo(playerNumber: Int, message: String): Boolean{
        //
        var success = false

        if(puppetList.isPuppetNumberInRange(playerNumber)){
            if(puppetList.isPuppetAlive(playerNumber)){
                success = mudOutput.addMsg(playerNumber, message)
            }
            else{
                //dead puppet
            }
        else{
            log.logError("Invalid player number in message:\n  " + Msg(playerNumber, message).look())
        }
        return success
    }

    fun msgAll(message: String): Boolean{
        var success = true
        val playerList = puppetList.getLivePuppetList()

        for(playerNumber in playerList){
            if(mudOutput.addMsg(playerNumber, message) == false){
                success = false
            }
        }
        return success
    }

    fun msgAllButOne(exclude: Int, message: String){
        var success = true
        val playerList = puppetList.getLivePuppetList(exclude)

        for(playerNumber in playerList){
            if(mudOutput.addMsg(playerNumber, message) == false){
                success = false
            }
        }
        return success
    }

    fun lookAround(playerNumber: Int): Boolean{
        var success = false
        var outputString = ""
        var location: Int
        //Returns -1 if playerNumber is out of range or notAlive
        location = puppetList.getPuppetLocation(playerNumber)
        if(location >= 0){
            outputString = roomList.lookAtRoom(location)
            outputString += puppetList.lookAtPuppetList(roomList.puppetsInRoom(location), playerNumber)
            success = msgTo(playerNumber, outputString)
            if(!success){
                //This might be the kind of thing to restart the server over
                //This also probably should be in MsgQueue class
                log.logError("Unable to add message to message queue: \n" + Msg(playerNumber, outputString).look())
            }
        }
        else{
            log.logError("Invalid player number: \n  lookAround(" + playerNumber.toString() + ")")
        }
        return success
    }

    fun move(playerNumber: Int, exitName: String): Boolean{
        //Create leaving and arriving messages and no exit messages
        var success = false
        var moveFrom: Int
        var moveTo: Int
        moveFrom = puppetList.getPuppetLocation(playerNumber)
        if(moveFrom >= 0){
            moveTo = roomList.whatRoomExitGoesTo(moveFrom, exitName)
            if(moveTo >= 0){
                //If it got to this point, playerNumber and exitName are correct
                success = true
                if(!roomList.removePuppetFromRoom(moveFrom, playerNumber){
                    success = false
                }
                //message about leaving and arriving
                if(!roomList.addPuppetToRoom(moveTo, playerNumber)){
                    success = false
                    //error 
                }
                if(!puppetList.changeLocation(playerNumber, moveTo)){
                    success = false
                }
            }
            //else{message You cannot go that way}
        }
        //else{error invalid player number}
        //if(!success){error move(number, exit) failed}
        return success
    }
    //fun oppositeDirection(direction: String): String{
    //    val directionList = in north northeast northwest up west out south southwest southeast down east
    //}
}
