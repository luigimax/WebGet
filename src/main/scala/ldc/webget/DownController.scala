/*
 * DownController.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._
import java.util.Timer
import java.util.TimerTask
//import db.{log4 => log}

//messages
case class InitActor(listener: Actor)
case class SetDowns(num: Int)
case class UpdateDowns
case class AddImg(img: String)
case class AddImgs(imgs: Queue[String])
case class AddBin(img: Img)
case class Ping
case class WantWork
case class NeedWork
case class NeedImgWork
case class WorkDoneLink(str: String)
case class WorkDoneImg(img: Img, lnk: String)
case class CleanUp
//Data Class
case class Img(desired: String, actual: String)

object DownController extends Actor {
    var downs: Int = 1
    var dl: String = ""
    //val centralQ = new Queue[String]
    val actList = new HashSet[Actor]
    //val centralImg = new Queue[Img]
    //val done = new HashSet[String]
    //val doneImg = new HashSet[Img]
    val origin = "Download Controller"
    val time = new Timer

    def act = {
        loop{
            react{
                case SetDowns(num)=>
                    downs = num
                case AddImgs(imgs)=>
                    cQ2(imgs)
                    reply(Ping)
                case AddImg(img) =>
                    println("Got img")
                    cQ(img)
                    reply(Ping)
                case NeedWork =>
                    val nex = nextQ
                    logcQ(nex)
                    reply(AddImg(nex))
                case NeedImgWork =>
                    val nex = nextI
                    logcI(nex)
                    reply(AddBin(nex))
                case WorkDoneLink(str: String) =>
                    cQ(str)
                    checkDist
                case WorkDoneImg(img, lnk) =>
                    cI(img)
                    cQ(lnk)
                    checkDist
                case Ping =>
                    makeActors
                    checkDist
                case CleanUp =>
                    killActors
                case oneIn(key)=>
                    println("down oneIn")
                    val si = oneOut(mangaSeq(key))
                    reply(si)

            }
        }
    }

    def checkDist = {
        for(x <- actList){
            x ! WantWork
        }
        //println("Check Dist Done")
    }

    def logcQ(send: String) ={
        val c = db.countCentQ
        if(c > 0){
            log("report", origin, "centralQ: %s sending %s".format(c,send))
        }
    }

    def logcI(send: Img) ={
        val c = db.countImgQ
        //println(c)
        if(c > 0){
            log("report", origin, "imgQ: %s sending %s".format(c,send.desired))
        }
    }

    def log(state: String, origin: String, body: String)={
        DataController ! Log4Me(state, origin, body)
    }

    def makeActors = {
        val rep = (actList.size until downs)
        for(c <- rep){
            val a = new DownActor
            actList += a
            a ! InitActor(this)
            a.origin = "HTML Parser: %s".format(c)
            log("report", origin, "Actor init Done: %s".format(a.origin))
            //println("Actor init Done: HTML")
        }
        for(c <- rep){
            val r = new BinGetActor
            r.origin = "BIN Parser: %s".format(c)
            actList += r
            r ! InitActor(this)            
            log("report", origin, "Actor init Done: %s".format(r.origin))
            //println("Actor init Done: Bin")
        }
        time.schedule(new CheckRe, 1000, 1000)
        
    }
    

    def killActors = {
        actList.foreach((c) => c.exit)
    }

    def nextQ : String = {
        //println("centralQ: "+ centralQ.size)
        //*
        val res = DataController !? GetCentQ
        res match {
            case "" => return "empt"
            case e => return e.toString
        }
        //*/
       /*
      if(centralQ.isEmpty){
        return "empt"
      }else{
          val ret = centralQ.dequeue
        return ret
      }
      */
      
    }

    def nextI : Img ={
        val img = DataController !? GetImgQ
        img match {            
            case Img(x,y) =>
                if(x == "empt" || y == "empt"){
                    //log("report", origin, "Empty empty")
                }else{
                    log("report", origin, "Img: %s - %s".format(x,y))
                }
                return Img(x,y)
        }
        /*
        if(centralImg.isEmpty){
            return Img("empt","empt")
        }else{
            val ret = centralImg.dequeue
            return ret
        }
        */
    }

    def cQ(add: String) = {
            
            //if(!done.contains(add)){
            //*
            //import ldc.webget.db.Item
            val rec = db.checkDone(add)
            println(add)
            rec match {
                case "empt" => DataController ! CentQ(add)
                case e => 
            }

            //*/
            checkDist
            /*
            if(!done.contains(add)){
                centralQ += add
                done += add
            }else{
                //println("gotThat") // way to many results
                //log("report", origin, "Got This: %s".format(add))
                checkDist
            }
            */
        
    }

    def cQ2(add: Queue[String]) ={
        add.foreach(cQ(_))

    }

    def cI(img: Img) = {
        //val res = DataController !? Done(img.toString)
            //println(res.toString)
            /*
            res match {
                case Unique =>
                    DataController ! ImgQ(img.desired,img.actual)
                    log("report", origin, "Unique Image added: %s".format(img.desired))
                    
                case NonUni =>
                    
                    //println("NonUni "+ add)
            }
            */
            checkDist


        /*
        if(!doneImg.contains(img)){
            centralImg += img
            doneImg += img
            log("report", origin, "Unique Image added: %s".format(img))
        }
        checkDist
        */
    }

    def setDl(loc: String) = {
        this.dl = loc
    }

    def mangaSeq(key: String): Queue[String]= {
        log("report", origin, "MangaSeq: %s".format(key))
        val manga = new NetParse
        //val s = url + key
        manga.parse(key)
        val mXml = manga.xml
        val url = "http://www.onemanga.com"
        var q = new Queue[String]

        val td = mXml \\ "td"
        val a = td \ "a"
        a.foreach((x) =>{
                val ro = x.attribute("href")
                ro match{
                    case Some(res) => q.enqueue(url + res.toString)
                    case _ => false
                }
        })
        log("report", origin, "MangaSeq Result: %s".format(q.toString))
        return q
    }

    start
}
