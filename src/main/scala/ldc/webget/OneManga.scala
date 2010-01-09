/*
 * OneManga.scala
 *
 * Author: Luke Harvey
 * purpose: The module (not a module yet - module api not implemented)
    OneManga downloads the specifyed manga frome the website onemanga.com

    Future plan: Implement module api and traits to seperate onemanga from
    the rest of the program and allow for easy extenstion (the origonal plan)
    -refactor the package to reflect the project name
    (artifact from first stage)
 */

package ldc.webget

import scala.xml._
import scala.collection.mutable._
import scala.actors.Actor
import scala.actors.Actor._

case class oneIn(key: String)
case class oneOut(q: Queue[String])

class OneManga {
    //url - the location of website
    val url = "http://www.onemanga.com"
    //net - holds the NetParse wraper
    val net = new NetParse

    //xml - hold the title directory xml
    var xml:Node = <head/>
    //folders - 
    var folders = new HashMap[String, String]
    var urlIndex = new Queue[String]

    val oneAct = actor{
        loop{
            react{
                case oneIn(key) =>
                    println("One manga oneIn")
                    DownController ! oneIn(key)
                case oneOut(q) =>
                    urlIndex ++= q.reverse
                    println("One manga oneOut")
                    run
                    println("angelDone")
                    DownController ! SetDowns(20) //this command needs a scale down
                    DownController !? AddImgs(urlIndex)
                    DownController ! Ping
            }
        }
    }

    //this is used to start downloading
    def run = {
        //titleSeq
        //mangaSeq("/12_Prince/")
        //mangaSeq("/Ane_Doki/")
        //mangaSeq("/Veritas/")
	//mangaSeq("/Freezing/")
	//mangaSeq("/ARISA/")
	//mangaSeq("/Hadashi_de_Bara_wo_Fume/")
	//mangaSeq("/Liar_Game/")
	//mangaSeq("/Dollgun/")
	//mangaSeq("/Kimi_no_Iru_Machi/")
	//mangaSeq("/Mother_Keeper/")
        //mangaSeq("/Code_Breaker/")
        //mangaSeq("/Mahou_Sensei_Negima!/")
        //mangaSeq("/Shut_Hell/")
        //urlIndex.foreach(println(_))
        

    }

    //download the titles listing
    def titleSeq = {
        
        net.parse(url+"/directory")
        xml = net.xml
        
        var a: Node = <head/>
        val tables = xml \\ "table"
        val td = tables \\ "td"

        var subj = ""
        td.foreach((x) =>{
                var chap = ""
            if((x \ "@class").text == "ch-subject"){
                a = (x \ "a")(0)
                subj = (a \ "@href").text
                folders += subj -> chap
            }
            if((x \ "@class").text == "ch-chapter"){

                    chap = x.text
                    folders(subj) = chap
                    
            }
            //folders += subj -> chap

        })

        //val key = folders.keys
        //key.foreach((sub) => println(sub + ":" + folders(sub)))
    }

    //download the initial chapter links for initial parseing
    def mangaSeq(key:String) = {
        val s = url + key
        oneAct ! oneIn(s)
    }

    //puts the title into a swing list
    def titleSeqList:javax.swing.DefaultListModel ={
        import scala.util.Sorting
        val get = new javax.swing.DefaultListModel
        titleSeq
        val gets = db.getAHash("om_getlist").split(",")
        gets.foreach((e) =>{folders -- gets})
        val key = folders.keys
        key.foreach((sub) => get.addElement(sub))
        
        return get
    }

    //kills all the actors to close program
    def clean = {
        DownController ! CleanUp
    }

}

