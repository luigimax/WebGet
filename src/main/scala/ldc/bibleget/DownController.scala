/*
 * DownController.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._

//messages
case class InitActor(listener: Actor)
case class SetDowns(num: Int)
case class UpdateDowns
case class AddImg(img: String)
case class AddImgs(imgs: Queue[String])
case class AddPages(page: String)
case class Ping
case class WantWork
case class NeedWork
case class WorkDoneLink(str: String)
case class WorkDoneImg(img: Img, lnk: String)
case class CleanUp
//Data Class
case class Img(actual: String, desired: String)

object DownController extends Actor {
    var downs: Int = 5
    val centralQ = new Queue[String]
    val actList = new HashSet[Actor]
    val centralImg = new HashSet[Img]
    val done = new HashSet[String]

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
                    reply(AddImg(nextQ))
                case WorkDoneLink(str: String) =>
                    cQ(str)
                    checkDist
                case WorkDoneImg(img, lnk) =>
                    centralImg += img
                    cQ(lnk)
                    checkDist
                case Ping =>
                    makeActors
                    checkDist
                case CleanUp =>
                    killActors

            }
        }
    }

    def distDowns = {
        for(x <- actList){
            x ! AddImg(nextQ)
        }
    }

    def checkDist = {
        for(x <- actList){
            x ! WantWork
        }
        //println("Check Dist Done")
    }

    def makeActors = {
        for(c <- (actList.size until downs)){
            val a = new DownActor
            actList += a
            a ! InitActor(this)
            println("Actor init Done")
        }
    }

    def killActors = {
        actList.foreach((c) => c.exit)
    }

    def nextQ : String = {
        //println("centralQ: "+ centralQ.size)
      if(centralQ.isEmpty){
        return "empt"
      }else{
          val ret = centralQ.dequeue
        return ret
      }
    }

    def cQ(add: String) = {
        if(!done.contains(add)){
            centralQ += add
            done += add
        }else println("gotThat")
    }

    def cQ2(add: Queue[String]) ={
        add.foreach(cQ(_))
    }

    start
}
