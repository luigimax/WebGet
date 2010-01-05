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
        -Item: case class for Querying for an Item
        -logUpdate: scala voodoo to get a java func in this class
===================================================
    */
    Class.forName("org.hsqldb.jdbcDriver")
    implicit val conn = connect("jdbc:hsqldb:file:db/webget;shutdown=true", "SA", "")
    var Qmax = 0
    var Qprog = 0

    case class Hash(key: String, value: String) {
        def toXML = <hash id={key}>{value}</hash>
    }
    case class Log(state: String, origin: String, body: String)
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

        //for (val person <- query("select * from hash", rs => Hash(rs,rs)))
        //    println(person.toXML)

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
        Qmax = Qmax + add
        logUpdate(Qprog,Qmax)
        println(Qprog + " " + Qmax)
    }

    def addQprog(add: Int) ={
        Qprog = Qprog + add
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
     Also See:
        db.<Initialization Section:>
===================================================
    */
    def log(state: String, origin: String, body: String)={
        implicit val s: Statement = conn << ""
        println(origin)
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
