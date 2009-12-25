/*
 * BinGetActor.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._

class BinGetActor extends Actor {
    var work = false
    var control: Actor = actor()
    val q = new Queue[Img]
    val bin = new BinDown
    val empt = Img("empt","empt")
    
    def act = {
        loop{
            react{
                case WantWork =>
                    reply(NeedImgWork)
                case InitActor(act)=>
                    control = act
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

    def doWork(img: Img) = {
        work = true
        val er = img.desired.split("-")
        val dir = DownController.dl + er(0)
        bin.mkdir(dir)
        bin.down(img.actual, dir+"/"+img.desired)
        println("Got: "+ img.desired)
        work = false
    }

    start

}
