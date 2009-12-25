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
    var curent: String = ""
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
      val cur = q.dequeue
          if(cur != "empt"){
            val lst = cur.split("/").reverse
            println ("LIST SIZE: " +lst.size)
            if (lst.size <= 3) println("CUR: "+cur)

            try {
            curent = cur
            net.parse(cur)
            xm = net.xml
            if(lst.size >= 2){
                if(lst(2) == "www.onemanga.com"){
                    atChap
                }
            }
            if (lst.size >= 3){
                if(lst(3) == "www.onemanga.com"){
                    atPage(lst)
                }
            }
            }catch{
                case e => println("Error From: "+ cur + "-------------------------------------")
            }
            
          }
          
      work = false
    }

    def atChap = {
        val ul = xm \\ "ul"
        val a = ul \\ "a"
        val lnk = a(0) \ "@href"
        println("Parse: "+lnk)
        control ! WorkDoneLink("http://www.onemanga.com" + lnk.toString)
        if(!q.isEmpty) control ! NeedWork
        true
    }

    def atPage(lst: Array[String]) = {
        val div = xm \\ "div"
        atPageList
        div.foreach((c) => {

                    val ri = c \ "@class"
                    if(ri == "one-page"){
                        val a = c \ "a"
                        val href = a \ "@href"
                        val img = c \\ "img"
                        val src = img \ "@src"
                        var l1 = lst(1)
                        for(c <- (lst(1).length until 4 )){
                            l1 = "0"+ l1
                        }
                        var l2 = lst(0)
                        if(l2.contains("-")){

                            var n = l2.split("-")
                            l2 = n(0)
                        }
                        for(c <- (l2.length until 4 )){
                            l2 = "0"+ l2
                        }
                        lst(2) match {
                            case "/" => control ! NeedWork
                            case "#" => control ! NeedWork
                            case _ =>
                                val actu = lst(2)+"-ch"+l1+"-p"+l2+".jpg"
                                println("Parse: "+actu)
                                control ! WorkDoneImg(Img(actu, src.toString), "http://www.onemanga.com" + href.toString)
                        }
                        //val actu = lst(2)+"-ch"+l1+"-p"+l2+".jpg"
                        //println("Parse: "+actu)
                        //control ! WorkDoneImg(Img(actu, src.toString), "http://www.onemanga.com" + href.toString)
                    }

        })
        //println(div)
        if(!q.isEmpty) control ! NeedWork
        true
    }

    def atPageList = {

        //println ("atPageList")
        val lst = curent.split("/").reverse
        //println (lst)
        var series = lst(2)
        var chp = lst(1)
        val sel = xm \\ "select"
        sel.foreach((c) => {

                val name = c \\ "@name"
                if (name == "page"){
                    val opt = c \\ "option"
                    opt.foreach((cc) =>{

                            val value = cc \ "@value"
                            val ret = "http://www.onemanga.com/" + series + "/"+ chp + "/" + value
                            control ! WorkDoneLink(ret)
                            true

                        })
                    
                }
                

        })
       

        //println ("end atPageList")
    }

    start
}


