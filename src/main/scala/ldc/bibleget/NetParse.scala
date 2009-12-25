/*
 * NetParse.scala
 *
 * Author: Luke Harvey
 *
    purpose: A wraper for TagSoup library
    When parse is called it downloads the html and converts it
    to Xhtml and stores it in -xml-
 */

package ldc.bibleget

import java.net._
import java.io._
import _root_.java.io.Reader

import org.xml.sax.InputSource

import scala.xml._

class NetParse {

    var xml: Node = <head/>
    

    def parse(sUrl:String) = {
        var url = new URL(sUrl)
        var connect = url.openConnection

        var sorce:InputSource = new InputSource
        var neo = new TagSoupFactoryAdapter //load sUrl
        var input = connect.getInputStream

        sorce.setByteStream(input)
        xml = neo.loadXML(sorce)
        input.close
    }



}
