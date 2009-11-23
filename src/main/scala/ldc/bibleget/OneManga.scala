/*
 * OneManga.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import scala.xml._
import scala.collection.mutable._

class OneManga {
    val url = "http://www.onemanga.com"
    val net = new NetParse
    
    var xml:Node = <head/>
    var folders = new HashMap[String, String]
    var urlIndex = new Queue[String]

    def run = {
        titleSeq
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
        println("angelDone")
	DownController ! SetDowns(20)
        DownController !? AddImgs(urlIndex)
        DownController ! Ping

    }

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

    def mangaSeq(key:String) = {
        val manga = new NetParse
        val s = url + key
        manga.parse(s)
        val mXml = manga.xml

        var q = new Queue[String]

        val td = mXml \\ "td"
        val a = td \ "a"
        a.foreach((x) =>{
                val ro = x.attribute("href")
                ro match{
                    case Some(res) => q.enqueue(url + res.toString)
                    case _ => false
                }
        })
        urlIndex ++= q.reverse
    }

}

