//Logger, I kinda want an error counter in here that can be checked periodically
//Possibly need to make it work with threads
//Need to handle file errors and figure out where to send error messages in that case
//Check to see if logs directory exists and create if it doesn't

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger(){
    var dateString = ""
    var timeString = ""
    var errorCount = 0

    @Synchronized fun setDateAndTimeStrings(){
        var dateTime = LocalDateTime.now()

        dateString = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
        timeString = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @Synchronized fun logError(message: String){
        //Writes message to log file beginning the line with "(Error)"
        //Also increments errorCount
        var logFile: File

        setDateAndTimeStrings()
        errorCount++
        logFile = File("logs/" + dateString + ".txt")
        if(!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Error) " + message + "\n")
    }

    @Synchronized fun logInfo(message: String){
        var logFile: File

        setDateAndTimeStrings()
        logFile = File("logs/" + dateString + ".txt")
        if(!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Info) " + message + "\n")
    }
}