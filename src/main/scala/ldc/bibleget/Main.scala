package ldc.bibleget

import scala.xml.Node

object Main {
  def main(args:Array[String]): Unit = {
      println("Big World")
      val url = "http://www.animesuki.com/"
      var net: NetParse = new NetParse
      
      println(net.parse(url))
      true
    }
}
