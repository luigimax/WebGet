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

    def run = {
        titleSeq
        mangaSeq("/Angel_Voice/")
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
        println(mXml)
    }

}

