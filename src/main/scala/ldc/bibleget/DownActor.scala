/*
 * DownActor.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._

class DownActor extends Actor {
    var work = false
    var control: Actor = actor()
    var net = new NetParse
    var xm: Node = <head/>
    val q = new Queue[String]

    def act = {
        loop{
            react{
                case InitActor(act)=> 
                    control = act
                    workCheck
                case WantWork =>
                    workCheck
                case AddImg(img) =>
                    q.enqueue(img)
                    doWork
            }
        }
    }

    def workCheck = {
        if (!work) control ! NeedWork
    }

    def doWork = {
      work = true
      net.parse(q.dequeue)
      xm = net.xml

      work = false
    }
}


