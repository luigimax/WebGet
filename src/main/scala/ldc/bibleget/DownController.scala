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
case class AddImgs(imgs: HashSet[String])
case class AddPages(page: String)

object DownController extends Actor {
    var downs: Int = 5

    def act = {
        loop{
            react{
                case SetDowns(num)=> downs = num
                case AddImgs(imgs)=> true
            }
        }
    }

    start
}
