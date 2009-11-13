package ldc.bibleget

import scala.xml.Node
import scala.xml.parsing.XhtmlParser

object Main {
  def main(args:Array[String]): Unit = {
      println("Big World")
      val url = "http://www.onemanga.com/directory/"
      val one = new OneManga
      //one.run

      val reo = new BinDown
      reo.mkdir("Angel_voice/")
      reo.down("http://media.onemanga.com/mangas/00000478/00000001/02.jpg", "Angel_voice/Angel_voice-c001-p02.jpg")
      
      
      true
    }
}
