//do I need isPuppetAlive()?
//I need a function to return a list of live puppet numbers
//I need a function to find first alive = false puppet or numberOfPuppets
//log messages for errors are probably ready to go now
//If I use this same class for NPCs and monsters, I would want it to grow so
//the idea I might as well just create a bunch of puppets for players at the
//start means it is not flexable for other uses
//Add a list of all currently live puppets

class PuppetList(logger: Logger){

    var numberOfPuppets: Int
    var puppetList: MutableList<Puppet> = mutableListOf()
    var log = logger

    init{
        numberOfPuppets = 0
        //puppetList = mutableListOf()
    }

    fun isPuppetNumberInRange(number: Int): Boolean{
        var retValue = false

        if(number >= 0 && number < numberOfPuppets){
            retValue = true
        }
        return retValue
    }

    fun nextAvailablePuppetNumber(): Int{
        //probably don't need this
        var retValue = numberOfPuppets
        for(puppet in puppetList){
            if(puppet.puppetAlive == false){
                retValue = puppet.puppetNumber
                break
            }
        }
        return retValue
    }

    fun setPuppet(number: Int, name: String, description: String, location: Int): Boolean{{
        //Set member variables, this may change or be removed in the future
        var success = false
        if(number >= 0 && number <= numberOfPuppets){
            if(number == numberOfPuppets){
                puppetList.add(Puppet(numberOfPuppets))
                numberOfPuppets++
            }
            if(puppetList[number].puppetAlive == false){
                puppetList[number].puppetAlive = true
                puppetList[number].puppetName = name
                puppetList[number].puppetDescription = description
                puppetList[number].puppetLocation = location
                success = true
            }
            else{
                log.logError("Tried to setPuppet() for a living puppet: \n  setPuppet(" + number.toString() + ", " + name + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  setPuppet(" + number.toString() + ", " + name + ")")
        }
    }

    fun lookAtPuppet(number: Int): String{
        //log error
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                return puppetList[number].look()
            }
            else{
                log.logError("Attempting to look at dead puppet: \n  lookAtPuppet(" + number.toString() + ")")
                return "There is a ghost here.\n"
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  lookAtPuppet(" + number.toString() + ")")
            return "There is a ghost here.\n"
        }
    }

    fun lookAtPuppetList(puppets: List<Int>, exclude: Int = -1): String{
        //Turns a list of player numbers into a string made up of calls to look() for each
        //exclude value suppresses a particular number (usually your number since you don't see yourself)
        var retString = ""
        if(numberOfPuppets > 0){
            for(puppet in puppets){
                if(puppet != exclude){
                    retString = retString + lookAtPuppet(puppet)
                }
            }
        }
        else{
            retString = "Funny, no one is here.\n"
        }
        return retString
    }

    fun getLivePuppetList(exclude: Int = -1): List<Int>{
        //This doesn't seem very efficient
        var retList: MutableList<Int>

        retList = mutableListOf()
        for(puppet in puppetList){
            if(puppet.puppetAlive == true){
                if(puppet.puppetNumber != exclude){
                    retList.add(puppet.puppetNumber)
                }
            }
        }
        return retList.toList()
    }

    fun killPuppet(number: Int): Boolean{
        //Used when a puppet isn't needed anymore
        var killed = false
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                puppetList[number].puppetAlive = false
                killed = true
            }
            else{
                log.logError("Attempted to kill a dead puppet: \n  killPuppet(" + number.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  killPuppet(" + number.toString() + ")")
        }
        return killed
    }

    fun getPuppetName(number: Int): String{
        //Returns the name of a puppet given its number or "NameError" if there is an error
        var retString = ""
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                retString = puppetList[number].puppetName
            }
            else{
                log.logError("Attempted to get the name of a dead puppet: \n  getPuppetName(" + number.toString() + ")")
                retString = "NameError"
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  getPuppetName(" + number.toString() + ")")
            retString = "NameError"
        }
        return retString
    }

    fun changePuppetName(number: Int, newName: String): Boolean{
        //Changes a puppets name given number and new name, returns a boolean for success
        var success = false
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                puppetList[number].puppetName = newName
                success = true
            }
            else{
                log.logError("Attempted to change the name of a dead puppet: \n  changePuppetName(" + number.toString() + ", " + newName + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  changePuppetName(" + number.toString() + ", " + newName + ")")
        }
        return success
    }

    fun changePuppetDescription(number: Int, newDesc: String): Boolean{
        //Changes a puppets description given number and new description, returns a boolean for success
        var success = false
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                puppetList[number].puppetDescription = newDesc
                success = true
            }
            else{
                log.logError("Attempted to change the description of a dead puppet: \n  changePuppetDescription(" + number.toString() + ", " + newDesc + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  changePuppetDescription(" + number.toString() + ", " + newDesc + ")")
        }
        return success
    }

    fun getPuppetLocation(number: Int): Int{
        //log error
        var retValue = -1
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                retValue = puppetList[number].puppetLocation
            }
            else{
                log.logError("Attempted to get the location of a dead puppet: \n  getPuppetLocation(" + number.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  getPuppetLocation(" + number.toString() + ")")
        }
        return retValue
    }

    fun changePuppetLocation(number: Int, location: Int): Boolean{
        //log error
        var success = false
        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                puppetList[number].puppetLocation = location
                success = true
            }
            else{
                log.logError("Tried to change the location of a dead puppet: \n  changePuppetLocation(" + number.toString() + ", " + location.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  changePuppetLocation(" + number.toString() + ", " + location.toString() + ")")
        }
        return success
    }
}
