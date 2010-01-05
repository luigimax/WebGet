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
}
