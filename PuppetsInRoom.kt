class PuppetsInRoom(){
    var puppets: MutableList<Int>

    init{
        puppets = mutableListOf()
    }

    fun findPuppet(number: Int): Boolean{
        //Checks if number is already in list.
        //I don't think a loop is necessary
        var found = false
        for(puppetNumber in puppets){
            if(puppetNumber == number){
                found = true
            }
        }
        return found
    }

    fun addPuppet(number: Int): Boolean{
        //Adds a puppet number to the list, returns false if it is in the list already.
        var addedToList = true
        if(findPuppet(number)){
            addedToList = false
        }
        else{
            puppets.add(number)
        }
        return addedToList
    }

    fun removePuppet(number: Int): Boolean{
        //Removes puppet from list, returns false if it wasn't in the list.
        var alreadyHere = true
        if(findPuppet(number)){
            puppets.remove(number)
        }
        else{
            alreadyHere = false
        }
        return alreadyHere
    }

    fun puppetNumbers(): List<Int>{
        //Returns a list of puppet numbers here right now.
        //I don't think a loop is necessary
        var numbers: MutableList<Int> = mutableListOf()
        for(puppet in puppets){
            numbers.add(puppet)
        }
        return numbers.toList()
    }

    fun look(): String{
        //List of puppets here as a string.
        return puppets.toString() + "\n"
    }
}
