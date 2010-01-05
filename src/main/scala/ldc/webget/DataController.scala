/*
---------------------------------------------------
db.scala:
    Purpose: The database class
    Author: Luke Harvey
 ---------------------------------------------------
*/

package ldc.webget

import scala.actors.Actor
import scala.actors.Actor._
import scala.util.matching.Regex
import db.{log, addQprog, addQmax}


case class Log4Me(state: String, origin: String, body: String)
case class Done(item: String)
case class ImgQ(desired: String, actual: String)
case class GetImgQ
case class centQ(item: String)
case class GetCentQ
//response classes
case class Unique
case class nonUni

object DataController extends Actor {
    /*
===================================================
DataConroller Event Listener Section:
    Purpose: Listens and responds to messages
    Used Case Classes:
        -Log4Me: (state: String, origin: String, body: String)
        -Done: (item: String)
        -ImgQ: (desired: String, actual: String)
        -GetImgQ:
        -centQ: (item: String)
        -Unique:
        -nonUni:
        -Img: (desired: String, actual: String)
    Methods:
        -act: the event loop
            +Log4Me: thread safe logger - calls db.log and DataController.event
                <pat>(state, origin, body) <line>case Log4Me(state, origin, body) =>
            +Done: Adds an item to Done table
                <pat>(item) <ret>Unique (or)nonUni <line>case Done(item)=>
            +ImgQ: adds an image to the database
                <pat>(desired, actual) <ret>Unique (or)nonUni <line>case ImgQ(desired, actual) =>
            +centQ: adds a url to the database
                <pat>(item) <ret>Unique (or)nonUni <line>case centQ(item) =>
            +GetCentQ: retreives a centQueue url
                <ret>String <line>case GetCentQ =>
            +GetImgQ: retreives an Img entry
                <ret>Img <line>case GetImgQ => reply(db.getImgQ)
===================================================
    */
    def act = {
        loop{
            react{
                case Log4Me(state, origin, body) =>
                    event(state,origin,body)
                    log(state,origin,body)
                case Done(item)=>
                    try{
                        db.addDone(item)
                        reply(Unique)
                    }catch{
                        case e => reply(nonUni)
                    }
                case ImgQ(desired, actual) =>
                    try{//false if its not unique
                        db.addImgQ(desired,actual)
                        reply(Unique)
                    }catch{
                        case e => reply(nonUni)
                    }
                case centQ(item) =>
                    try{//false if its not unique
                        //println(item)
                        db.addCentQ(item)
                        reply(Unique)
                    }catch{
                        case e => reply(nonUni)
                    }
                case GetCentQ =>
                    reply(db.getCentQ)
                case GetImgQ => reply(db.getImgQ)
            }
        }
    }

    start
    /*
===================================================
Event Section:
    Purpose: Event listener triggerd by log writes
    Methods:
        -event: event listener
            <pat>(state, origin, body)
===================================================
    */
    def event(state: String, origin: String, body: String)={
        val body_parse = """Parse: (.*)""".r
        val body_unique = "Unique Image added: (.*)".r
        val body_down = """Downloaded Img: (.*)""".r
        val body_write = """File Writen: (.*)""".r
        val body_retrival = """Retrival of (.*)""".r
        val body_exist = """(.+) already exists""".r
        val body_exist2 = "File Exists".r
        val body_gotThat = "Got This: (.*)".r
        state match {
            case _ => None
        }
        origin match {
            case _ =>
               
        }
        body match {
            case body_parse(x) =>
                //addQmax(1)
                //println("parse")
            case body_down(x) =>
                addQprog(1)
                //println("Downimg")
            case body_retrival(x)=>
                addQprog(1)
                //println("Retrival")
            case body_exist(x) =>
                addQprog(1)
                //println("exist")
            case body_gotThat(x) =>
                addQprog(1)
                //println("got that")
            case body_write(x) =>
                println("write")
            case body_exist2(x) =>
            case body_unique(x) =>
                addQmax(1)
                //println("Unique")
           
                
            case _ => //println("Fail")
        }

    }
}
