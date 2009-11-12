package ldc.bibleget

import scala.xml.Node
import scala.xml.parsing.XhtmlParser

object Main {
  def main(args:Array[String]): Unit = {
      println("Big World")
      val url = "http://www.onemanga.com/directory/"
      val one = new OneManga
      one.run
      
      
      println()
      true
    }
}
