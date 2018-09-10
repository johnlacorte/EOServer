/**
 * This holds all the data for a Puppet, which is used to add living type things to the MUD
 *
 * The constructor creates a Puppet with mostly default values because their would need to be at
 * least three different constructors for different situations. New player, new notaplayer, player
 * loaded from file, and notplayer loaded from file and they likely would change a lot as I add
 * some early features. So for now I'm just creating this one puppet and changing the couple values
 * for the things I need.
 *
 * @param puppetNumber The number of this puppet, not used for anything other than to have a unique value.
 * @property playerPuppet Is the puppet a player? Default:false
 * @property puppetAlive Is the puppet alive? Default:false
 * @property puppetName Default name is "Default Name"
 * @property puppetDescription Describes what activity the puppet is engaged in. Default is " is here."
 * @property puppetHome The number of the room to begin in when created or if they died and suddenly they aren't dead anymore.
 * @property puppetLocation Number of the room they currently are in. Default:0
 * @property puppetListener If this is a player, this will be set to their connection number, otherwise -1.
 */
class Puppet(val puppetNumber: Int){
    var playerPuppet: Boolean
    var puppetAlive: Boolean
    var puppetName: String
    var puppetDescription: String
    var puppetHome: Int
    var puppetLocation: Int
    var puppetListener: Int

    init{
        playerPuppet = false
        puppetAlive = false
        puppetName = "DefaultName"
        puppetDescription = " is here."
        puppetHome = 0
        puppetLocation = 0
        puppetListener = -1
    }

    /**
     * Quick look at Puppet.
     *
     * @returns A string, something like "Puppet is here.\n" if it's name is Puppet.
     */
    fun look(): String{
        return puppetName + puppetDescription + "\n"
    }
}
