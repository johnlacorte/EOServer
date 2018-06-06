//Logger, I kinda want an error counter in here that can be checked periodically
//I would like an open() and close() function to have errors from a single task be
//grouped under the same timestamp if it doesn't make using it complicated in the rest of the code
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Logger(){
    //File might be better in the function
    var dateString = ""
    var timeString = ""
    var errorCount = 0

    fun setDateAndTimeStrings(){
        var dateTime = LocalDateTime.now()
        //Class DateTimeFormatter
        dateString = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE)
        timeString = dateTime.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    fun logError(message: String){
        var logFile: File
        setDateAndTimeStrings()
        errorCount++
        logFile = File("logs/" + dateString + ".txt")
        
        if(!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Error) " + message + "\n")
    }

    fun logInfo(message: String){
        var logFile: File
        setDateAndTimeStrings()
        logFile = File("logs/" + dateString + ".txt")
        
        if(!logFile.exists()){
            logFile.createNewFile()//returns a success boolean
        }
        logFile.appendText(timeString + " (Info) " + message + "\n")
    }
}