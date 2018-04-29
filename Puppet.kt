//Constructor is meant to be called with just the puppetNumber then when
//you want to use a puppet that has already been created, use setPuppet().
//it's likely that these default arguments will be removed and most of the
//time these things will be loaded from files.
//Any other stats can go in here including things like inventory.

class Puppet(number: Int, name: String = "DefaultName",
            description: String = " is here.", location: Int = 0){

    var puppetNumber: Int
    var puppetName: String
    var puppetDescription: String
    var puppetLocation: Int

    init{
        puppetNumber = number
        puppetName = name
        puppetDescription = description
        puppetLocation = location
    }

    fun setPuppet(name: String, description: String, location: String){
        //Set member variables, this may change or be removed in the future
        puppetName = name
        puppetDescription = description
        puppetLocation = location
    }

    fun look(): String{
        //A quick look at puppet like in "look" used in a room.
        return puppetName + puppetDescription
    }

    //save()
    //load()
}
