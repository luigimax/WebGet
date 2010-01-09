/*
 * DownActor.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import scala.collection.mutable.{HashMap, HashSet, Queue}
import scala.actors.Actor
import scala.actors.Actor._
import scala.xml._
//import db.{log4 => log}

class DownActor extends Actor {
    var work = false
    var control: Actor = actor()
    var net = new NetParse
    var xm: Node = <head/>
    var curent: String = ""
    val q = new Queue[String]
    var origin = ""

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
    def log(state: String, origin: String, body: String)={
        DataController ! Log4Me(state, origin, body)
    }

    def workCheck = {
        //implicit def unbox(i: Unit) = i.
        if (!work) control ! NeedWork
        //val d = DataController !? GetCentQ
        val d = db.getCentQ
        if(d != ""){q.enqueue(d)}
        if(!q.isEmpty) doWork
        //println("check")
    }

    def doWork = {
      work = true
      val cur = q.dequeue
          if(cur != "empt"){
            val lst = cur.split("/").reverse
            log("debug", origin,"LIST SIZE: " +lst.size)
            //println ("LIST SIZE: " +lst.size)
            if (lst.size <= 3) log("debug", origin,"CUR: "+cur)

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
                case e => 
                    val err = "An error occured. <!>current unit of work: %s <!>my xm: %s <!>Stacktrace: %s".format(this.curent,this.xm,e.toString)
                    log("error", origin, err)
            }
            
          }
          
      work = false
    }

    def atChap = {
        val ul = xm \\ "ul"
        val a = ul \\ "a"
        val lnk = a(0) \ "@href"
        log("report", origin,"Parse: "+lnk)
        log("debug", origin,"REDUCED TO UL: %s  <!>REDUCED TO A: %s <!>REDUCED TO LNK:".format(ul,a,lnk))
        //println("Parse: "+lnk)
        //control ! WorkDoneLink("http://www.onemanga.com" + lnk.toString)
        doneIfy("http://www.onemanga.com" + lnk.toString)
        DataController !? Done(curent)
        //control ! Ping
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
                                //println("Parse: "+actu)
                                log("report", origin, "Parse: "+actu)
                                DataController !? ImgQ(actu, src.toString) match{
                                    case Unique => log("report", origin, "Unique Image added: %s".format(actu))
                                }
                                DataController !? Done(curent)
                                doneIfy("http://www.onemanga.com" + href.toString)
                                //control ! WorkDoneImg(Img(actu, src.toString), "http://www.onemanga.com" + href.toString)
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

    def doneIfy(item: String) ={
        val rec =db.checkDone(item)
         rec match {
              case "empt" => DataController ! CentQ(item)
              case e => 
         }
         //control ! Ping
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
                            //control ! WorkDoneLink(ret)
                            doneIfy(ret)
                            DataController !? Done(curent)
                            true

                        })
                    
                }
                

        })
       

        //println ("end atPageList")
    }

    start
}


