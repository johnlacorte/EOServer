//World is a bridge between rooms contained in a RoomContainer and puppets
//I think I want a queue of pairs of puppetNumbers and outputStrings in here
//This should take an argument to decide to start with an empty world or
//load files
import java.util.*

class World(howManyConnections: Int){
    //I need a create a RoomContainer object
    //I need to create a Puppet array
    //I need to create a MessageQueue
    //maybe a queue for broadcasts
    //I need functions for everything you can do
    var MAX_CONNECTIONS: Int
    var world: World
    var puppetArray: Array<Puppet>
    var messageQueue = Queue<Pair<Int, String> >

    init{
        MAX_CONNECTIONS = howManyConnections
        world = World()
        //puppetArray = Array<Puppet>(MAX_CONNECTIONS)
    }

        //if(!roomPuppets.isEmpty()){
        //    for(puppet in roomPuppets){
        //        val description = puppet.look(puppetNumber)
        //        if(description != ""){
        //            retString = retString + puppet.look(puppetNumber) + "\n"
        //        }
        //    }
        //}
}
