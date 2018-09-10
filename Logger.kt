//TODO Need to handle file errors and figure out where to send error messages in that case
//TODO Check to see if logs directory exists and create if it doesn't

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * An object to log error messages and info to a file.
 *
 * Logger creates a file with the current date as a name and adds the time before each message.
 * This uses the standard locks to prevent calls from different threads from messing up read and
 * write operations. The silly way I get the date and time strings was to avoid writing really long
 * lines of code.
 * @property errorCount The number of errors logged since Logger was constructed.
 */
class Logger(){
    var dateString = ""
    var timeString = ""
    var errorCount = 0

    @Synchronized private fun getDateAndTimeStrings(){
        var dateTime = LocalDateTime.now()

        dateString = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
        timeString = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    /**
     * Writes a string to the day's log file, prefixing it with the time and "(Error)", and incrementing errorCount.
     * @param message The error message.
     */
    @Synchronized fun logError(message: String){
        var logFile: File

        getDateAndTimeStrings()
        errorCount++
        logFile = File("logs/" + dateString + ".txt")
        if (!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Error) " + message + "\n")
    }

    /**
     * Writes a string to the day's log file, prefixing it with the time and "(Info)".
     * @param message The info message.
     */
    @Synchronized fun logInfo(message: String){
        var logFile: File

        getDateAndTimeStrings()
        logFile = File("logs/" + dateString + ".txt")
        if (!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Info) " + message + "\n")
    }
}