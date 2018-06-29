//do I need isPuppetAlive()?
//newPuppet(), newPlayerPuppet(), and clonePuppet() need error messages fixed
//and might be better to use my getters and setters wherever possible

class PuppetList(logger: Logger){
    var numberOfPuppets: Int
    var puppetList: MutableList<Puppet> = mutableListOf()
    var log = logger

    init{
        numberOfPuppets = 0
        //puppetList = mutableListOf()
    }

    fun isPuppetNumberInRange(number: Int): Boolean{
        //Since most member functions take a Puppet number
        var retValue = false

        if(number >= 0 && number < numberOfPuppets){
            retValue = true
        }
        return retValue
    }

    fun nextAvailablePuppetNumber(): Int{
        //Puppet numbers for Puppets that are playerPuppet and !puppetAlive are reused before creating new ones
        var retValue = numberOfPuppets
        for(puppet in puppetList){
            if(puppet.playerPuppet == true && puppet.puppetAlive == false){
                retValue = puppet.puppetNumber
                break
            }
        }
        return retValue
    }

    fun newPlayerPuppet(listener: Int, name: String, home: Int): Int{
        //Recycles old unused puppet or creates new one and returns it's number
        var number: Int

        number = nextAvailablePuppetNumber()
        if(number == numberOfPuppets){
            puppetList.add(Puppet(numberOfPuppets))
            numberOfPuppets++
        }
        if(puppetList[number].puppetAlive == false){
            puppetList[number].playerPuppet = true
            puppetList[number].puppetAlive = true
            puppetList[number].puppetName = name
            puppetList[number].puppetDescription = " is here."
            puppetList[number].puppetHome = home
            puppetList[number].puppetLocation = home
            puppetList[number].puppetListener = listener
        }
        else{
            log.logError("Tried to setPuppet() for a living puppet: \n  setPuppet(" + number.toString() + ", " + name + ")")
        }
        return number
    }

    //fun loadPlayerPuppet(listener: Int, name: String): Int{

    //}

    //savePlayerPuppet

    //loadPuppetList savePuppetList

    fun newPuppet(name: String, home: Int): Int{
        //Create a puppet that is not a player
        var number: Int

        number = nextAvailablePuppetNumber()
        if(number == numberOfPuppets){
            puppetList.add(Puppet(numberOfPuppets))
            numberOfPuppets++
        }
        if(puppetList[number].puppetAlive == false){
            puppetList[number].playerPuppet = false
            puppetList[number].puppetAlive = true
            puppetList[number].puppetName = name
            puppetList[number].puppetDescription = " is here."
            puppetList[number].puppetHome = home
            puppetList[number].puppetLocation = home
            puppetList[number].puppetListener = -1
        }
        else{
            log.logError("Tried to setPuppet() for a living puppet: \n  setPuppet(" + number.toString() + ", " + name + ")")
        }
        return number
    }

    fun clonePuppet(puppetNumber: Int, home: Int): Int{
        var number: Int

        number = nextAvailablePuppetNumber()
        if(number == numberOfPuppets){
            puppetList.add(Puppet(numberOfPuppets))
            numberOfPuppets++
        }
        if(puppetList[number].puppetAlive == false){
            puppetList[number].playerPuppet = false
            puppetList[number].puppetAlive = true
            puppetList[number].puppetName = getPuppetName(puppetNumber)
            puppetList[number].puppetDescription = " is here."
            puppetList[number].puppetHome = home
            puppetList[number].puppetLocation = home
            puppetList[number].puppetListener = -1
        }
        else{
            log.logError("Tried to setPuppet() for a living puppet: \n  setPuppet(" + number.toString() + ")")
        }
        return number
    }

    fun setPuppet(number: Int, name: String, description: String, location: Int): Boolean{
        //Set member variables, this may change or be removed in the future
        //Probably remove this because I have better ideas of what I will need now
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
        return success
    }

    fun lookAtPuppet(number: Int): String{
        //Returns short description of a Puppet
        var retString = "There is a ghost here.\n"

        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                retString = puppetList[number].look()
            }
            else{
                log.logError("Attempting to look at dead puppet: \n  lookAtPuppet(" + number.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  lookAtPuppet(" + number.toString() + ")")
        }
        return retString
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

    fun killPuppet(number: Int): Boolean{
        //Used when a puppet isn't needed anymore and number can be reused
        var killed = false

        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                //flip boolean for playerPuppet to recycle puppetNumber
                puppetList[number].playerPuppet = true
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
        var retString = "NameError"

        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                retString = puppetList[number].puppetName
            }
            else{
                log.logError("Attempted to get the name of a dead puppet: \n  getPuppetName(" + number.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  getPuppetName(" + number.toString() + ")")
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
        //Returns puppet location
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
        //Changes puppet location
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

    fun getPuppetListener(number: Int): Int{
        //log error
        var retValue = -1

        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                retValue = puppetList[number].puppetListener
            }
            else{
                log.logError("Attempted to get the listener of a dead puppet: \n  getPuppetListener(" + number.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  getPuppetListener(" + number.toString() + ")")
        }
        return retValue
    }

    fun changePuppetListener(number: Int, listener: Int): Boolean{
        //log error
        var success = false

        if(isPuppetNumberInRange(number)){
            if(puppetList[number].puppetAlive == true){
                puppetList[number].puppetListener = listener
                success = true
            }
            else{
                log.logError("Tried to change the listener of a dead puppet: \n  changePuppetListener(" + number.toString() + ", " + listener.toString() + ")")
            }
        }
        else{
            log.logError("Puppet number is out of range: \n  changePuppetListener(" + number.toString() + ", " + listener.toString() + ")")
        }
        return success
    }
}
