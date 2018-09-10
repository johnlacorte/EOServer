//I think an integer set would be great for keeping reuseable puppetNumbers.
//There are three basic type of error messages: expected a live puppet but found a dead one, expected a dead puppet but
//found a live one, and puppet number out of range. Writing all these was mind numbing.
/**
 * A collection of [Puppet] objects. May or may not be a linked list.
 *
 * @param log A [Logger] object for error messages.
 * @property numberOfPuppets How many [Puppet] objects have been added. May including puppets where the player has disconnected.
 */
class PuppetList(var log: Logger){
    var numberOfPuppets: Int
    var puppetList: MutableList<Puppet> = mutableListOf()

    init{
        numberOfPuppets = 0
    }

    /**
     * Used to check puppetNumbers.
     *
     * Checks if a number is less than zero or more than the highest [Puppet] number.
     *
     * @param puppetNumber The number to check.
     * @returns Returns true or false.
     */
    fun isPuppetNumberInRange(puppetNumber: Int): Boolean{
        var retValue = false

        if (puppetNumber >= 0 && puppetNumber < numberOfPuppets){
            retValue = true
        }
        return retValue
    }

    /**
     * Checks for a puppetNumber not being used anymore.
     *
     * Puppets that playerPuppet == true and puppetAlive == false are players who have left the game or
     * disconnected. Currently these are the only ones that are recycled to prevent the size of PuppetList
     * growing way to large. I might be able to do better than the way I've done this.
     *
     * @returns A number < numberOfPuppets or a number == numberOfPuppets.
     */
    fun nextAvailablePuppetNumber(): Int{
        var retValue = numberOfPuppets
        for (puppet in puppetList){
            if (puppet.playerPuppet == true && puppet.puppetAlive == false){
                retValue = puppet.puppetNumber
                break
            }
        }
        return retValue
    }

    /**
     * Creates a new player puppet.
     *
     * This will reuse a puppetNumber from a previous player that has disconnected or get a new one.
     *
     * @param listener The number of the connection to pass output to.
     * @param name The name of the new player.
     * @param home This is the number of the room the player starts in and possibly respawns to after death.
     * @returns Number of the new puppet.
     */
    fun newPlayerPuppet(listener: Int, name: String, home: Int): Int{
        var number: Int

        number = nextAvailablePuppetNumber()
        if (number == numberOfPuppets){
            puppetList.add(Puppet(numberOfPuppets))
            numberOfPuppets++
        }
        if (puppetList[number].puppetAlive == false){
            puppetList[number].playerPuppet = true
            puppetList[number].puppetAlive = true
            puppetList[number].puppetName = name
            puppetList[number].puppetDescription = " is here."
            puppetList[number].puppetHome = home
            puppetList[number].puppetLocation = home
            puppetList[number].puppetListener = listener
        }
        else {
            log.logError("Tried to reuse a living puppet: \n  PuppetList.newPlayerPuppet(${listener.toString()}, $name , ${home.toString()})")
        }
        return number
    }

    //fun loadPlayerPuppet(listener: Int, name: String): Int{

    //savePlayerPuppet

    //loadPuppetList savePuppetList

    /**
     * Creates a [Puppet] that is not a player.
     *
     * This is meant to add puppets to the world to be a permanent part of it. At the moment they do absolutely
     * nothing.
     *
     * @param name The name of the new puppet.
     * @param home The [Room] it starts in.
     * @returns The puppet number of the new puppet.
     */
    fun newPuppet(name: String, home: Int): Int{
        var number: Int

        number = nextAvailablePuppetNumber()
        if (number == numberOfPuppets){
            puppetList.add(Puppet(numberOfPuppets))
            numberOfPuppets++
        }
        if (puppetList[number].puppetAlive == false){
            puppetList[number].playerPuppet = false
            puppetList[number].puppetAlive = true
            puppetList[number].puppetName = name
            puppetList[number].puppetDescription = " is here."
            puppetList[number].puppetHome = home
            puppetList[number].puppetLocation = home
            puppetList[number].puppetListener = -1
        }
        else {
            log.logError("Tried to reuse a living puppet: \n  newPuppet(" + name + ", " + home.toString() + ")")
        }
        return number
    }

    /**
     * Makes a copy of a [Puppet].
     *
     * This is meant to create several similar puppets. Not meant to copy players but at this point
     * it shouldn't create any problems but in the future if it does I will have to add a check for that
     * and it might not work.
     *
     * @param home The room the copy begins in.
     * @param puppetNumber The number of the puppet to copy.
     * @returns The new puppet number of the new puppet.
     */
    fun clonePuppet(home: Int, puppetNumber: Int): Int{
        var number = -1

        if (isPuppetNumberInRange(puppetNumber)){
            number = nextAvailablePuppetNumber()
            if (number == numberOfPuppets){
                puppetList.add(Puppet(numberOfPuppets))
                numberOfPuppets++
            }
            if (puppetList[number].puppetAlive == false){
                puppetList[number].playerPuppet = false
                puppetList[number].puppetAlive = true
                puppetList[number].puppetName = getPuppetName(puppetNumber)
                puppetList[number].puppetDescription = " is here."
                puppetList[number].puppetHome = home
                puppetList[number].puppetLocation = home
                puppetList[number].puppetListener = -1
            }
            else {
                log.logError("Tried to reuse a living puppet: \n  PuppetList.clonePuppet(${puppetNumber.toString()}, ${home.toString()})")
                number = -1
            }
        }
        else {
            log.logError("Puppet number out of range: \n  PuppetList.clonePuppet(${puppetNumber.toString()}, ${home.toString()})")
        }
        return number
    }

    //fun setPuppet(number: Int, name: String, description: String, location: Int): Boolean{
        //Set member variables, this may change or be removed in the future
        //Probably remove this because I have better ideas of what I will need now
        //var success = false

        //if (number >= 0 && number <= numberOfPuppets){
            //if (number == numberOfPuppets){
                //puppetList.add(Puppet(numberOfPuppets))
                //numberOfPuppets++
            //}
            //if (puppetList[number].puppetAlive == false){
                //puppetList[number].puppetAlive = true
                //puppetList[number].puppetName = name
                //puppetList[number].puppetDescription = description
                //puppetList[number].puppetLocation = location
                //success = true
            //}
            //else {
                //log.logError("Tried to setPuppet() for a living puppet: \n  PuppetList.setPuppet(${number.toString()}, $name)")
            //}
        //}
        //else {
            //log.logError("Puppet number is out of range: \n  PuppetList.setPuppet(" + number.toString() + ", " + name + ")")
        //}
        //return success
    //}

    /**
     * Quick look at a puppet.
     *
     * If there are any problems this will return "There is a ghost here." and log an error message.
     *
     * @param number The number of the [Puppet] you want to look at.
     * @returns A string, something like "PuppetName is here."
     */
    fun lookAtPuppet(number: Int): String{
        var retString = "There is a ghost here.\n"

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                retString = puppetList[number].look()
            }
            else {
                log.logError("Attempting to look at dead puppet: \n  PuppetList.lookAtPuppet(${number.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.lookAtPuppet(${number.toString()})")
        }
        return retString
    }

    /**
     * This takes a list of puppet numbers and returns a string built from calling lookAtPuppet for each.
     *
     * Since you don't want to see yourself in every room this takes a number, exclude, to skip adding that.
     * There isn't a situation I can think of for this to be called with an empty list, it is probably a bug
     * but if that happens it will return the string "Funny, no one is here."
     *
     * @param puppets A list of Int that contains the numbers of the [Puppet] objects.
     * @param exclude A puppet number, usually the [Puppet] looking, to exclude from being added.
     * @returns A string.
     */
    fun lookAtPuppetList(puppets: List<Int>, exclude: Int = -1): String{
        var retString = ""

        if (numberOfPuppets > 0){
            for(puppet in puppets){
                if(puppet != exclude){
                    retString = retString + lookAtPuppet(puppet)
                }
            }
        }
        else {
            retString = "Funny, no one is here.\n"
        }
        return retString
    }

    /**
     * Used to find the puppetNumber of a [Puppet] in a [Room] by name.
     *
     * Giving an empty list or an occurrence value less than one will return a -1.
     * If the list contains any numbers outside the range of puppetNumbers not only
     * will getPuppetName() log an error but this will as well that includes the values
     * in the list at the time it was discovered.
     *
     * @param puppets A list of puppetNumbers in a [Room].
     * @param puppetName The name to search for.
     * @param occurrence 1 = first match, 2 = second match, etc.
     * @returns The puppetNumber or -1.
     */
    fun getPuppetNumberFromName(puppets: List<Int>, puppetName: String, occurrence: Int): Int{
        var puppetNumber = -1
        var occCounter = 0

        if (occurrence > 0){
            for (puppet in puppets){
                var name = getPuppetName(puppet)
                if (name == puppetName){
                    occCounter++
                    if (occCounter == occurrence){
                        puppetNumber = puppet
                        break
                    }
                }
                else {
                    if (name == "NameError"){
                        log.logError("PuppetList.getPuppetNumberFromName() failed: \n  name=$puppetName puppetList=${puppets.toString()}")
                        break
                    }
                }
            }
        }
        return puppetNumber
    }

    /**
     * Makes a puppet's puppetAlive == false.
     *
     * Currently there is no way for a puppet to die unless it is a player that leaves the game or a non player
     * that is removed by a command. This function does this. Dying from injuries will need to be handled a little
     * differently. At the moment, the plan is if a player leaves the game the puppetNumber will be reused for the
     * next player to connect or the next non player puppet created. If they actually die, remove them from the room,
     * possibly drop some items, and place them somewhere where dead players go and their puppetAlive value is never
     * changed to false. Non player puppets that are removed by a command are recycled the way a player leaving the
     * game is. As a simple way to cause them to behave like a player who has left the game I have set playerPuppet to
     * true and set puppetAlive to false. In case a non player puppet were to die, it will simply be removed from the
     * room and will stay in memory. It's playerPuppet will still be false, because I want to keep it and it's puppetAlive
     * will be false to cause it to be inactive and to catch any bugs where some bit of code thinks it's alive still.
     *
     * @param number The number of player puppet that has left the game of non player puppet that is removed by command.
     * @returns Returns false if this fails.
     */
    fun killPuppet(number: Int): Boolean{
        var killed = false

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                //flip boolean for playerPuppet to recycle puppetNumber
                puppetList[number].playerPuppet = true
                puppetList[number].puppetAlive = false
                killed = true
            }
            else {
                log.logError("Attempted to kill a dead puppet: \n  PuppetList.killPuppet(${number.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.killPuppet(${number.toString()})")
        }
        return killed
    }

    /**
     * Get the name of a [Puppet].
     *
     * If it fails it logs errors "Attempted to get the name of a dead puppet" or
     * "Puppet number is out of range".
     *
     * @param number The number of the [Puppet] to get the name for.
     * @returns The name of the puppet or "NameError"
     */
    fun getPuppetName(number: Int): String{
        var retString = "NameError"

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                retString = puppetList[number].puppetName
            }
            else {
                log.logError("Attempted to get the name of a dead puppet: \n  PuppetList.getPuppetName(${number.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.getPuppetName(${number.toString()})")
        }
        return retString
    }

    /**
     * Changes the name of a [Puppet].
     *
     * @param number The number of the [Puppet] to change the name of.
     * @param newName The new name.
     * @returns Returns False if it fails.
     */
    fun changePuppetName(number: Int, newName: String): Boolean{
        var success = false

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                puppetList[number].puppetName = newName
                success = true
            }
            else {
                log.logError("Attempted to change the name of a dead puppet: \n  PuppetList.changePuppetName(${number.toString()}, $newName)")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.changePuppetName(${number.toString()}, $newName)")
        }
        return success
    }

    /**
     * Changes the quick description (usually " is here.") of a [Puppet].
     *
     * @param number The number of the [Puppet].
     * @param newDesc The new description.
     * @returns Returns false if it fails.
     */
    fun changePuppetDescription(number: Int, newDesc: String): Boolean{
        var success = false

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                puppetList[number].puppetDescription = newDesc
                success = true
            }
            else {
                log.logError("Attempted to change the description of a dead puppet: \n  PuppetList.changePuppetDescription(${number.toString()}, $newDesc)")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.changePuppetDescription(${number.toString()}, $newDesc)")
        }
        return success
    }

    /**
     * Gets the number of the [Room] a [Puppet] is in.
     *
     * @param number The number of the [Puppet]
     * @returns Their location or -1.
     */
    fun getPuppetLocation(number: Int): Int{
        var retValue = -1

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                retValue = puppetList[number].puppetLocation
            }
            else {
                log.logError("Attempted to get the location of a dead puppet: \n  PuppetList.getPuppetLocation(${number.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.getPuppetLocation(${number.toString()})")
        }
        return retValue
    }

    /**
     * Change the location of a [Puppet] to a different [Room] number.
     *
     * This doesn't actually remove them from the room, it only changes the number stored locally.
     *
     * @param number The number of the [Puppet].
     * @param location The number of a room.
     * @returns Returns false if it fails.
     */
    fun changePuppetLocation(number: Int, location: Int): Boolean{
        var success = false

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                puppetList[number].puppetLocation = location
                success = true
            }
            else {
                log.logError("Tried to change the location of a dead puppet: \n  PuppetList.changePuppetLocation(${number.toString()}, ${location.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.changePuppetLocation(${number.toString()}, ${location.toString()})")
        }
        return success
    }

    /**
     * Gets the number of the connection to send output to or -1 if not a player.
     *
     * If it fails logs "Attempted to get the listener of a dead puppet" or
     * "Puppet number is out of range"
     *
     * @param number The [Puppet] number.
     * @returns Returns connection number or -1.
     */
    fun getPuppetListener(number: Int): Int{
        var retValue = -1

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                retValue = puppetList[number].puppetListener
            }
            else {
                log.logError("Attempted to get the listener of a dead puppet: \n  PuppetList.getPuppetListener(${number.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.getPuppetListener(${number.toString()})")
        }
        return retValue
    }

    /**
     * Changes the listener of a [Puppet].
     *
     * This is meant to be used to initially used to set the listener to the connection number of a player. I would
     * not recommend using it for anything else because it will have no effect on the broadcastList that is used for
     * messages such as chat that are sent out to all players.
     *
     * @param number The number of the [Puppet].
     * @param listener The number of the connection that output will be sent to.
     * @returns Returns false if it fails.
     */
    fun changePuppetListener(number: Int, listener: Int): Boolean{
        var success = false

        if (isPuppetNumberInRange(number)){
            if (puppetList[number].puppetAlive == true){
                puppetList[number].puppetListener = listener
                success = true
            }
            else {
                log.logError("Tried to change the listener of a dead puppet: \n  PuppetList.changePuppetListener(${number.toString()}, ${listener.toString()})")
            }
        }
        else {
            log.logError("Puppet number is out of range: \n  PuppetList.changePuppetListener(${number.toString()}, ${listener.toString()})")
        }
        return success
    }
}
