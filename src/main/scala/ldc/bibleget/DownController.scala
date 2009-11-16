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

object DownController extends Actor {
    var downs: Int = 5
    val centralQ = new Queue[String]
    val actList = new HashSet[Actor]

    def act = {
        loop{
            react{
                case SetDowns(num)=>
                    downs = num
                case AddImgs(imgs)=>
                    centralQ ++= imgs
                case NeedWork => 
                    reply(AddImg(centralQ.dequeue))
                case WorkDoneLink(str: String) =>
                    reply(AddImg(centralQ.dequeue))
                    centralQ.enqueue(str)
                case Ping =>
                    makeActors
                    checkDist

            }
        }
    }

    def distDowns = {
        for(x <- actList){
            x ! AddImg(centralQ.dequeue)
        }
    }

    def checkDist = {
        for(x <- actList){
            x ! WantWork
        }
    }

    def makeActors = {
        for(c <- (actList.size until downs)){
            val a = new DownActor
            actList += a
            a ! InitActor(this)
        }
    }

    def nextQ : String = {
      if(centralQ.isEmpty){
        return "empt"
      }else{
        return centralQ.dequeue
      }
    }

    start
}
