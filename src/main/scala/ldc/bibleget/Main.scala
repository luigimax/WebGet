package ldc.bibleget

import scala.xml.Node
import scala.xml.parsing.XhtmlParser

object Main {
  def main(args:Array[String]): Unit = {
      println("Big World")
      val url = "http://bibliacat.pbworks.com/Bible-Research"
      var net: NetParse = new NetParse
      
      println(net.parse(url))
      true
    }
}
