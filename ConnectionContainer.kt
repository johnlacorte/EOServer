class ConnectionContainer(max: Int){
    //Ten connections in a list array or vector
    //Methods to access their member functions
    val connectionArray: Array<Connection>
    val MAX_CONNECTIONS: Int

    init{
        MAX_CONNECTIONS = max
        conectionArray = Array(MAX_CONNECTIONS, { i -> Connection(i)})
    }

    fun connection(n: Int): Connection?{
        if(n < MAX_CONNECTIONS){
            return connectionArray[n]
        }
        else{
            return null
        }
    }

    fun emptySlot(): Connection?{
        //It is possible that someone could disconnect after it is checked
        //but that's not a big deal
        for(con in connectionArray){
            if(!con.isConnected()){
                return con
            }
        }
        return null
    }
}
