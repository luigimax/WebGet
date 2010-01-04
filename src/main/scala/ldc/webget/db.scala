/*
 * db.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import java.sql.{DriverManager, Connection, ResultSet, PreparedStatement, Statement, Date }
import DriverManager.{getConnection => connect}
import ldc.webget.RichSQL._
import java.io.File
import scala.collection.mutable.{HashMap, HashSet, Queue}

object db {
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
        initialization funciton
        Tables:
            Hash - think of it exactly like a HashSet or a Map
                -key - the index
                -value - stored info
            log - stores system logs
                -state - what the log entry is reporting
                        -error - if its an error
                        -report - if its some part of the program reporting progress
                        -debug - for debug messages
                -origin - what made the report, use a unique meaningfull value
                -body - what the message is, default limit is set to 1megabyte
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

    def addHash(key: String, value: String) ={
        implicit val s: Statement = conn << ""
        val insertHash = conn prepareStatement "insert into Hash(key, value) values(?, ?)"
        insertHash<<key<<value<<!

    }

    def updateHash(key: String, value: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("update Hash set value='%s' where key='%s'".format(value,key))
    }

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

    def getHash: HashMap[String,String] ={
        implicit val s: Statement = conn << ""
        val ret = new HashMap[String,String]
        for (val hash <- query("select * from hash", rs => Hash(rs,rs))){
            ret += hash.key -> hash.value
        }
        return ret
    }
    def getAHash(target: String): String ={
        val h = getHash
        h.get(target) match {
            case None => return ""
            case Some(x) => return x
            case _ => return ""
        }
    }

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

    def addDone(item: String) ={
        implicit val s: Statement = conn << ""
        s.executeQuery("insert into Done values('%s')".format(item))
    }

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

    def log(state: String, origin: String, body: String)={
        implicit val s: Statement = conn << ""
        val insertLog = conn prepareStatement "insert into Log(state, origin, body) values(?, ?, ?)"
        insertLog<<state<<origin<<body<<!
    }

    def log4(state: String, origin: String, body: String)={
        DataController ! Log4Me(state, origin, body)
    }

    def KillDB ={
        implicit val s: Statement = conn << ""
        //s.executeQuery("delete from Done where *")
        s.executeQuery("SHUTDOWN")
    }
}
