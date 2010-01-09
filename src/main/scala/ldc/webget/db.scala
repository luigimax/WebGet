/*
---------------------------------------------------
db.scala:
    Purpose: The database class
    Author: Luke Harvey
 ---------------------------------------------------
*/

package ldc.webget

import java.sql.{DriverManager, Connection, ResultSet, PreparedStatement, Statement, Date }
import DriverManager.{getConnection => connect}
import ldc.webget.RichSQL._
import java.io.File
import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.util.matching.Regex

object db {
    /*
===================================================
Declare Section:
    Purpose: Declares Class wide variables
    Variables:
        -conn: connects to the database
        -Qmax: Ui progressbar max state var
        -Qprog: Ui progressbar current progress var
        -Hash: case class for Querying for a Hash
        -Log: case class for Querying for a Log
        -LogRegex: case class for filtering by regex patterns
        -Item: case class for Querying for an Item
        -logUpdate: scala voodoo to get a java func in this class
===================================================
    */
    Class.forName("org.hsqldb.jdbcDriver")
    implicit val conn = connect("jdbc:hsqldb:file:db/webget;shutdown=true", "SA", "")
    var Qmax = 0
    var Qprog = 0
    var Qcent = 0
    var Qimg = 0
    var Qdone = 0

    case class Hash(key: String, value: String) {
        def toXML = <hash id={key}>{value}</hash>
    }
    case class Log(state: String, origin: String, body: String)
    case class LogRegex(state: Regex, origin: Regex, body: Regex)
    case class Item(item: String)

    //scala voodoo to get a java func in this class
    var logUpdate = logUp((prog:Int,max:Int) => {})
    /*
===================================================
Initialization Section:
    Purpose: Deals with Database initialization
    Methods:
        -init: Initializes a new database in the event the database is deleted
            +Hash: -think of it exactly like a HashSet or a Map
                <line>CREATE TABLE Hash(
                 -key - the index
                 -value - stored info
            +Log: -stores system logs
                <line>CREATE TABLE log(
                  -state - what the log entry is reporting
                         -error - if its an error
                         -report - if its some part of the program reporting progress
                         -debug - for debug messages
                  -origin - what made the report, use a unique meaningfull value
                  -body - what the message is, default limit is set to 1megabyte of text
            +centQueue: -stores url to an un-parsed page
                <line>CREATE TABLE centQueue(
                        -item - index - contents
            +imgQueue: -stores un-downloaded images and their names
                <line>CREATE TABLE imgQueue(
                       -desired - index - what to name the file once it is downloaded
                       -actual - url to the image
            +Done: -stores the list of things that are complete for later comparison
                <line>CREATE TABLE Done(
                   -item - index - contents
===================================================
    */
    def init ={
        val seeF = new File("db/webget.script")
        var setup = Array("")
        if(!seeF.exists){
            setup = Array(
            """
            CREATE TABLE Hash(
            key varchar PRIMARY KEY,
            value varchar
            )
            """,
            """
            CREATE TABLE log(
            state varchar,
            origin varchar,
            body longvarchar
            )
            """,
            """
            CREATE TABLE centQueue(
            item varchar PRIMARY KEY
            )
            """,
            """
            CREATE TABLE imgQueue(
            desired varchar PRIMARY KEY,
            actual varchar
            )
            """,
            """
            CREATE TABLE Done(
            item varchar PRIMARY KEY
            )
            """
             )
            implicit val s: Statement = conn << setup
            s.executeQuery("SET WRITE_DELAY 0")
            //add in some default settings - can be updated later
            //addHash("username","")
            addHash("om_getlist","")
            addHash("om_filesDir","")
        }

        Qdone = countDone.toInt
        Qcent = countCentQ.toInt
        Qimg = countImgQ.toInt
        Qmax = Qdone - (Qcent + Qimg)


    }
    /*
===================================================
Database Clear functions:
    Purpose: Full delete of all records in specified table
    Methods:
        -wipeLog: deletes all items form table <Log>
        -wipeDone: deletes all items form table <Done>
        -wipeImgQ: deletes all items form table <imgQueue>
        -wipeCentQ: deletes all items form table <centQueue>
        -clear: calls all the wipe methods
===================================================
    */
    def wipeLog ={
       implicit val s: Statement = conn << ""
       s.executeQuery("delete from Log")
    }

    def wipeDone ={
        implicit val s: Statement = conn << ""
        s.executeQuery("delete from Done ")
    }

    def wipeImgQ ={
        implicit val s: Statement = conn << ""
        s.executeQuery("delete from imgQueue")
    }

    def wipeCentQ ={
        implicit val s: Statement = conn << ""
        s.executeQuery("delete from centQueue")
    }

    def clear ={
         wipeLog
         wipeDone
         wipeImgQ
         wipeCentQ
    }
    /*
===================================================
Hash Section:
    Purpose: all calls concerning The Hash table.
    Table Pattern: (key: String, value: String)
                    key = Table index
                    value = value of entry
    Methods:
        -addHash: adds a hash
            <pat>(key, value)
        -updateHash: updates an existing Hash
            <pat>(key, value)
        -getHash: returns all hashes in table
            <ret>HashMap[String,String]
        -getAHash: returns only specifyed Hash
            <pat>(key) <ret>String
===================================================
    */
    def addHash(key: String, value: String) ={
        implicit val s: Statement = conn << ""
        val insertHash = conn prepareStatement "insert into Hash(key, value) values(?, ?)"
        insertHash<<key<<value<<!

    }

    def updateHash(key: String, value: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("update Hash set value='%s' where key='%s'".format(value,key))
    }

    def getHash: HashMap[String,String] ={
        implicit val s: Statement = conn << ""
        val ret = new HashMap[String,String]
        for (val hash <- query("select * from hash", rs => Hash(rs,rs))){
            ret += hash.key -> hash.value
        }
        return ret
    }
    def getAHash(key: String): String ={
        val h = getHash
        h.get(key) match {
            case None => return ""
            case Some(x) => return x
            case _ => return ""
        }
    }
    /*
===================================================
Ui Update Section:
    Purpose: Has methods concerning Ui Status updates
    Methods:
        -addQmax: increment progressbar maximum by 1
                    <pat>(add: Int)
        -addQprog: increments progressbar peresent progress by 1 (how much is done)
                    <pat>(add: Int)
        -logUp: crazy scala voodoo to insert a function into
                a variable. The Ui update function, this is what calls it.
                    <pat>(func: (Int,Int) => Unit) <ret>function(Int,Int) => Unit
===================================================
    */
    def addQmax(add: Int)={
        Qmax = Qcent + Qimg
        //var n = ((countCentQ + countImgQ) - countDone)
        //Qmax = Qmax + add
        logUpdate(Qprog,Qmax)
        println(Qprog + " " + Qmax)
    }

    def addQprog(add: Int) ={
        //var n = countDone
        //Qprog = Qprog + add
        Qprog = Qdone
        logUpdate(Qprog,Qmax)
        println(Qprog + " " + Qmax)
    }

    def logUp(func: (Int, Int) => Unit): (Int, Int) => Unit ={
        new Function2[Int,Int,Unit]{
            def apply(prog:Int,max:Int) = {
                func(prog,max)
            }
        }
    }
    /*
===================================================
Program Properties Section:
    Purpose: deals with all persistant properties - properties are stored in
             the Hash table
    Methods:
        Property:
            om_getlist:
                -setGetlist: adds an entry to the list
                    <pat>(lst :String)
                -getlist: returns DefaultListModel for ui sycronization
                    <ret>javax.swing.DefaultListModel
                -dropList: remove an item form the list
                    <pat>(target: String)
===================================================
    */
    def setGetlist(lst :String)={
        var h = getAHash("om_getlist")
        if(h == ""){
            h = lst
        }else{
            h = "%s,%s".format(h,lst)
        }
        updateHash("om_getlist",h)
    }

    def getlist:javax.swing.DefaultListModel ={
        var lst = new javax.swing.DefaultListModel
        getAHash("om_getlist").split(",").foreach((e)=>{
            if(e != ""){lst.addElement(e)}
        })
        return lst
    }

    def dropList(target: String) ={
        var h = ""
        getAHash("om_getlist").split(",").foreach((e)=>{
            if(e != target && target != ""){
                if(h == ""){
                    h = e
                }else{
                    h = "%s,%s".format(h,e)
                }
            }
            //println(e)
        })
         updateHash("om_getlist",h)
    }
    /*
===================================================
Done Section:
    Purpose: deals with the done list
    Table Pattern: (item: String)
                   item = what is added
    Methods:
        -addDone: adds an item to the done table
            <pat>(item)
===================================================
    */
    def addDone(item: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("insert into Done values('%s')".format(item))
        Qdone = Qdone +1
    }

    def countDone: Long ={
        implicit val s: Statement = conn << ""
        var cn:Long = 0
        for (val it <- query("select * from Done", rs => Item(rs))){
            cn = cn+1
        }
        cn
    }

    def checkDone(item:String): String ={
        implicit val s: Statement = conn << ""
        var ret = "empt"
        for (val it <- query("select * from Done where item='%s'".format(item), rs => Item(rs))){
            ret = it.item
        }
        ret
    }
    /*
===================================================
Img Queue Section:
    Purpose: deals with the Central Img Queue (imgQueue)
    Table Pattern: (desired: String, actual: String)
                    desired = what the image will be named
                    actual = the full url to the image
    Used Case Classes:
        -Img: (desired: String, actual: String)
    Methods:
        -addImgQ: adds an image entry to the table
            <pat>(desired, actual)
        -countImgQ: returns the number of entries in the table
            <ret>Long
        -getImgQ: returns the next image entry. Will retrun Img("empt","empt") if none found
            <ret>Img
===================================================
    */
    def addImgQ(desired: String, actual: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("insert into imgQueue values('%s', '%s')".format(desired,actual))
        Qimg = Qimg +1
    }

    def countImgQ: Long ={
        implicit val s: Statement = conn << ""
        var cn:Long = 0
        for (val it <- query("select * from imgQueue", rs => Img(rs,rs))){
            cn = cn+1
        }
        cn
    }

    def getImgQ:Img ={
        implicit val s: Statement = conn << ""
        var st = Img("empt","empt")
        for (val img <- query("select top 1 * from imgQueue", rs => Img(rs,rs))){
            st = img
            //return img
            //println(st.desired +" getImgQ")
            s.executeQuery("delete from imgQueue where desired='%s'".format(img.desired))
            //Qimg = Qimg - 1
        }
        st
    }
    /*
===================================================
Central Queue Section:
    Purpose: deals with the Central Queue (centQueue)
    Table Pattern: (item: String)
                    item = url to page for parsing
    Returned Case Classes:
        Img: (desired: String, actual: String)
    Methods:
        -addImgQ: adds an image entry to the table
            <pat>(desired, actual)
        -countImgQ: returns the number of entries in the table
            <ret>Long
        -getImgQ: returns the next image entry. Will retrun Img("empt","empt") if none found
            <ret>Img
===================================================
    */
    def addCentQ(item: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("insert into centQueue values('%s')".format(item))
        Qcent = Qcent +1
    }

    def countCentQ: Long ={
        implicit val s: Statement = conn << ""
        var cn:Long = 0
        for (val it <- query("select * from centQueue", rs => Item(rs))){
            cn = cn+1
        }
        cn
    }

    def getCentQ:String ={
        implicit val s: Statement = conn << ""
        var st = ""
        for (val it <- query("select top 1 item from centQueue", rs => Item(rs))){
            st = it.item
            //println(st +" getCentQ")
            s.executeQuery("delete from centQueue where item='%s'".format(st))
            //Qcent = Qcent -1
        }       
        st
    }
    /*
===================================================
Logs Section:
    Purpose: deals with the Log table
    Table Pattern: (state: String, origin: String, body: String)
                    state = urgency level - debug,error,report
                    origin = where the log entry came from
                    body = body of the log
    Methods:
        -log: adds a log entry
            <pat>(state, origin, body)
        -log4: sends log request to the DataController. Used inorder to maintain thread safety
            <pat>(state, origin, body)
        -getOrigins: returns a list of origins for the ui log viewer
            <ret>javax.swing.DefaultListModel
        -getOrigins: returns a filtered list of origins based on a exact match
            <pat>(filter: Log) <ret>javax.swing.DefaultListModel
        -getOrigins: returns a filtered list of origins based on a exact match
            <pat>(filter: LogRegex) <ret>javax.swing.DefaultListModel
        -getFilterLog: returns a log based on exact strings
            <pat>(filter:Log) <ret>HashSet[Log]
        -getRegexLog: returns a log based on a regex pattern
            <pat>(filter:LogRegex) <ret>HashSet[Log]
        -filterState: returns a HashSet of log entries filtered by state
            <pat>(state) <ret>HashSet[Log]
        -filterOrigin: returns a HashSet of log entries filtered by origin
            <pat>(origin) <ret>HashSet[Log]
        -filterStateOrigin: returns a HashSet of log entries filtered by state and origin
            <pat>(state, origin) <ret>HashSet[Log]
     Also See:
        db.<Initialization Section:>
===================================================
    */
    def log(state: String, origin: String, body: String)={
        implicit val s: Statement = conn << ""
        //println(origin)
        val insertLog = conn prepareStatement "insert into Log(state, origin, body) values(?, ?, ?)"
        insertLog<<state<<origin<<body<<!
    }

    def log4(state: String, origin: String, body: String)={
        DataController ! Log4Me(state, origin, body)
    }

    def getOrigins:javax.swing.DefaultListModel={
        implicit val s: Statement = conn << ""
        val ret = new javax.swing.DefaultListModel
        val hset = new HashSet[String]
        for (val log <- query("select * from Log", rs => Log(rs,rs,rs))){
            hset += log.origin
        }
        hset.foreach(ret.addElement(_))
        return ret
    }

    def getOrigins(filter: Log):javax.swing.DefaultListModel={
        val ret = new javax.swing.DefaultListModel
        var hset = getFilterLog(filter)
        hset.foreach((e) => ret.addElement(e.body))
        return ret
    }

    def getOrigins(filter: LogRegex):javax.swing.DefaultListModel={
        val ret = new javax.swing.DefaultListModel
        var hset = getRegexLog(filter)
        hset.foreach((e) => ret.addElement(e.body))
        return ret
    }

    def getFilterLog(filter:Log):HashSet[Log]={
        implicit val s: Statement = conn << ""
        val hset = new HashSet[Log]
        var q = "select * from Log"

        if(filter != Log("","","")) q concat " where"
        if(filter.state != "") q concat " state='%s'".format(filter.state)
        if(filter.state != "") q concat " origin='%s'".format(filter.origin)
        if(filter.state != "") q concat " body='%s'".format(filter.body)
        for (val log <- query(q, rs => Log(rs,rs,rs))){
            hset += log
        }
        return hset
    }

    def getRegexLog(filter:LogRegex):HashSet[Log] ={
        implicit val s: Statement = conn << ""
        val hset = new HashSet[Log]
        
        if(filter == LogRegex("".r,"".r,"".r)) return hset
        for (val log <- query("select * from Log", rs => Log(rs,rs,rs))){
            hset += log
        }
        val blank = "".r
        val finHset = new HashSet[Log]
        hset.foreach((e) => {
            if(regMatch(filter.state, e.state) && regMatch(filter.origin, e.origin) && regMatch(filter.body, e.body)){
                finHset += e
            }
        })
    
        return finHset
    }

    def regMatch(pattern: Regex, str: String):Boolean ={
        if (pattern == "".r) return true
        if (pattern.findFirstIn(str) != Some(None)) return true
        return false
    }

    def filterState(state: String): HashSet[Log] ={
        implicit val s: Statement = conn << ""
        val hset = new HashSet[Log]
        for (val log <- query("select * from Log where state='%s'".format(state), rs => Log(rs,rs,rs))){
            hset += log
        }
        hset
    }

    def filterOrigin(origin: String): HashSet[Log]={
        implicit val s: Statement = conn << ""
        val hset = new HashSet[Log]
        for (val log <- query("select * from Log where origin='%s'".format(origin), rs => Log(rs,rs,rs))){
            hset += log
        }
        hset
    }

    def filterStateOrigin(state: String, origin: String): HashSet[Log] ={
        implicit val s: Statement = conn << ""
        val hset = new HashSet[Log]
        for (val log <- query("select * from Log where state='%s' and origin='%s'".format(state,origin), rs => Log(rs,rs,rs))){
            hset += log
        }
        hset
    }
    /*
===================================================
KillDB Section:
    Purpose: Issues the SHUTDOWN command to the hsqldb database
    Methods:
        -KillDB: Issues the SHUTDOWN command to the hsqldb database
===================================================
    */
    def KillDB ={
        implicit val s: Statement = conn << ""
        //s.executeQuery("delete from Done where *")
        s.executeQuery("SHUTDOWN")
    }
}
