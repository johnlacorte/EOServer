//This object is for writing messages to log files
//This needs to use mutexes or something so different threads don't try
//to write to the same file at the same time.
//I think I want to add the date to the filename and the time for each msg

class Logger(fileName: String){

    val logFile: String

    init{
        logFile = filename
    }

    fun logMessage(msg: String){
        //Open log file, append time and message, and close
    }
}
