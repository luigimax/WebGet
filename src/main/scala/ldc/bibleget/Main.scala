package ldc.bibleget



object Main {
  def main(args:Array[String]): Unit = {
      println("Big World")
      val url = "http://www.onemanga.com/directory/"
      val one = new OneManga
      //one.run

      import tfd.scala.squib._
      import tfd.scala.squib.event._

      import javax.swing.{JButton, WindowConstants}
      one.titleSeq
      
      var centerList = list("neo", 'text->"super")

        frame(
     attributes( 'title -> "Hello",
                 'visible -> true,
                 'defaultCloseOperation -> WindowConstants.DISPOSE_ON_CLOSE),
                 'layout->gridlayout('rows->3, 'columns->1),
     contents(
         button (
                'text->"Big buttion of DOOOOOM",
                actionPerformed { 
                  label.id("zig").setText("string")
                }),
         label( "zig" ,'text->"Super zig da zig on zig. Take off all your zig!"),
         centerList
     )
 ).pack

      
      true
    }
}
