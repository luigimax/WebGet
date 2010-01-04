/*
 * BinGetActor.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
import java.io.File
//import db.{log4 => log}

class BinGetActor extends Actor {
    var work = false
    var control: Actor = actor()
    val q = new Queue[Img]
    val bin = new BinDown
    val empt = Img("empt","empt")
    var origin = ""
    
    def act = {
        loop{
            react{
                case WantWork =>
                    reply(NeedImgWork)
                case InitActor(act)=>
                    control = act
                    bin.setOrigin(origin)
                    workCheck
                case AddBin(img) =>
                    if(img != empt) {
                        q.enqueue(img)
                        doWork(img)
                    }
                    
            }
        }
    }

    def workCheck = {
        if (!work) control ! NeedWork
    }

    def log(state: String, origin: String, body: String)={
        DataController ! Log4Me(state, origin, body)
    }

    def doWork(img: Img) = {
        work = true
        val er = img.desired.split("-")
        val dir = DownController.dl + er(0)
        val f = dir+"/"+img.desired
        log("report", origin, "Beggining Download of: "+ img.desired)
        
        if (!checkFile(f)){
            if(!checkFile(dir))bin.mkdir(dir)
            bin.down(img.actual, f)
            if (checkFile(f)){
                log("report", origin, "Downloaded Img: "+ img.desired)
            }else{
                log("report", origin, "Retrival of %s failed".format(img.desired))
            }
            //println("Got: "+ img.desired)
        }else{
             log("report", origin, "%s already exists".format(img.desired))
        }
        work = false
    }

    def checkFile(fName: String): Boolean={
        val seeF = new File(fName)
        return seeF.exists
    }

    start

}
