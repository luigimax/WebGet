/*
 * DataController.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
    def act = {
        loop{
            react{
                case Log4Me(state, origin, body) =>
                    event(state,origin,body)
                    log(state,origin,body)
                case Done(item)=>
                    try{//false if its not unique
                         //println("done "+item)
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

    start
}
