/*
---------------------------------------------------
DataControllerSpec.scala:
    Purpose: Test object for the DataController
    Author: Luke Harvey
 ---------------------------------------------------
*/

package ldc.webget

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4, ScalaTestSuite}
import ldc.webget.db.log
//import ldc.webget.DataController.{NonUni}

class DataControllerSpecTest extends JUnit4(DataControllerSpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object DataControllerSpecRunner extends ConsoleRunner(DataControllerSpec)

object DataControllerSpec extends Specification {
    def mepass ={true must beTrue}
    def mefail ={false must beTrue}
    "Datacontroller event" should {
        doBefore { db.clear }
        "not increase Qmax or Qprog for match Parse: something" in{
            db.Qmax = 0
            DataController.event("","not increase Qmax or Qprog for match Parse: something","Parse: Some stringOf doom.jpg")
            if(db.Qmax == 0){mepass}else mefail
        }
        //""
    }

    "Datacontroller Actor Done" should {
        doBefore { db.clear }
        "Reply Unique to a unique entry" in{
            DataController !? Done("A string") match{
                case Unique => mepass
                case NonUni => mefail
                case e =>
                    println(e.toString)
                    mefail
            }
        }

        "Reply NonUni to a non unique entry" in{
            DataController !? Done("A string")
            DataController !? Done("A string") match{
                case NonUni => mepass
                case e =>
                    println(e.toString)
                    mefail
            }
        }
    }

    "Datacontroller Actor ImgQ" should {
        "Reply Unique to a unique entry" in{
            DataController !? ImgQ("A string","d") match{
                case Unique => mepass
                case e =>
                    println(e.toString)
                    mefail
            }
        }
        "Reply NonUni to a non unique entry" in{
            DataController !? ImgQ("A string","d")
            DataController !? ImgQ("A string","d") match{
                case NonUni => mepass
                case e =>
                    println(e.toString)
                    mefail
            }
        }
        "Retreve an image entry" in{
            DataController !? ImgQ("A string","d")
            var img = DataController !? GetImgQ
            if(img == Img("A string","d")){mepass}else mefail
        }
        "Reply 'empt','empt' if no image in queue" in{
            var img = DataController !? GetImgQ
            if(img == Img("empt","empt")){mepass}else mefail
        }
    }

    "Datacontroller Actor CentQ" should {
        "Reply Unique to a unique entry" in{
            DataController !? CentQ("A string") match{
                case Unique => mepass
                case e =>
                    println(e.toString)
                    mefail
            }
        }
        "Reply NonUni to a non unique entry" in{
            DataController !? CentQ("A string")
            DataController !? CentQ("A string") match{
                case NonUni => mepass
                case e =>
                    println(e.toString)
                    mefail
            }
        }
        "Retreve an image entry" in{
            DataController !? CentQ("A string")
            var img = DataController !? GetCentQ
            if(img == "A string"){mepass}else mefail
        }
        "Reply 'empt' if no item in queue" in{
            var img = DataController !? GetCentQ
            if(img == ""){mepass}else mefail
        }
    }
}
