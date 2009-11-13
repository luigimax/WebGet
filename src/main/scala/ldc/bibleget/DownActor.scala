/*
 * DownActor.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import scala.collection.mutable.{HashMap, HashSet}
import scala.actors.Actor
import scala.actors.Actor._

class DownActor extends Actor {
    var work = false
    var control: Actor = actor()

    def act = {
        loop{
            react{
                case InitActor(act)=> 
                    control = act
                    workCheck
                case WantWork =>
                    workCheck
            }
        }
    }

    def workCheck = {
        if (!work) control ! NeedWork
    }
}


