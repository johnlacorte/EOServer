//Constructor is meant to be called with just the puppetNumber then when
//you want to use a puppet that has already been created, use setPuppet().
//it's likely that these default arguments will be removed and most of the
//time these things will be loaded from files.
//Any other stats can go in here including things like inventory.
//"home" location

class Puppet(number: Int){

    var puppetAlive: Boolean
    var puppetNumber: Int
    var puppetName: String
    var puppetDescription: String
    var puppetLocation: Int

    init{
        puppetAlive = false
        puppetNumber = number
        puppetName = "DefaultName"
        puppetDescription = " is here."
        puppetLocation = 0
    }

    fun look(): String{
        //A quick look at puppet like in "look" used in a room.
        return puppetName + puppetDescription + "\n"
    }

    //save()
    //load()
}
