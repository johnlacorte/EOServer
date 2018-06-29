//This is a simple list of integers to make looking up which puppets are in a room easier

class PuppetsInRoom(){
    var puppets: MutableList<Int>

    init{
        puppets = mutableListOf()
    }

    fun findPuppet(number: Int): Boolean{
        //Checks if number is already in list.
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
        var success = true

        if(findPuppet(number)){
            success = false
        }
        else{
            puppets.add(number)
        }
        return success
    }

    fun removePuppet(number: Int): Boolean{
        //Removes puppet from list, returns false if it wasn't in the list.
        var success = true

        if(findPuppet(number)){
            puppets.remove(number)
        }
        else{
            success = false
        }
        return success
    }

    fun puppetNumbers(): List<Int>{
        //Returns a list of puppet numbers here right now.
        return puppets.toList()
    }

    fun look(): String{
        //List of puppets here as a string just to check if it works.
        return puppets.toString() + "\n"
    }
}
