/*
 * NetParse.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import java.net._
import java.io._
import _root_.java.io.Reader

//import org.w3c.dom.Document
import org.xml.sax.InputSource

import scala.xml._

class NetParse {

    

    def parse(sUrl:String): Node = {
        var url = new URL(sUrl)
        var connect = url.openConnection

        var sorce:InputSource = new InputSource
        var neo = new TagSoupFactoryAdapter //load sUrl

        sorce.setByteStream(connect.getInputStream)
        neo.loadXML(sorce)

        //return neo.load(sUrl)
        //new HTMLCleanerFactoryAdapter getDOM(new BufferedReader(new InputStreamReader(connect.getInputStream)))
        //var in:BufferedReader = new BufferedReader(new InputStreamReader(connect.getInputStream))
       
    }
}
