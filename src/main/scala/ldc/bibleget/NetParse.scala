/*
 * NetParse.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.bibleget

import de.hars.scalaxml._

import java.net._
import java.io._
import _root_.java.io.Reader

import org.w3c.dom.Document

import scala.xml._

class NetParse {

    

    def parse(sUrl:String): Document = {
        var url = new URL(sUrl)
        var connect = url.openConnection

        //new TagSoupFactoryAdapter load sUrl
        new HTMLCleanerFactoryAdapter getDOM(new BufferedReader(new InputStreamReader(connect.getInputStream)))
        //var in:BufferedReader = new BufferedReader(new InputStreamReader(connect.getInputStream))
       
    }
}
