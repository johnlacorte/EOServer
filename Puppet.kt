//Constructor is meant to be called with just the puppetNumber then when
//you want to use a puppet that has already been created, use setPuppet().
//it's likely that these default arguments will be removed and most of the
//time these things will be loaded from files.
//Any other stats can go in here including things like inventory.

class Puppet(number: Int){
    var playerPuppet: Boolean
    var puppetAlive: Boolean
    var puppetNumber: Int
    var puppetName: String
    var puppetDescription: String
    var puppetHome: Int
    var puppetLocation: Int
    var puppetListener: Int

    init{
        playerPuppet = false
        puppetAlive = false
        puppetNumber = number
        puppetName = "DefaultName"
        puppetDescription = " is here."
        puppetHome = 0
        puppetLocation = 0
        puppetListener = -1
    }

    fun look(): String{
        //A quick look at puppet like in "look" used in a room.
        return puppetName + puppetDescription + "\n"
    }

    //save()
    //load()
}
