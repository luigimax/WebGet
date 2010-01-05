/*
---------------------------------------------------
MySpec.scala:
    Purpose: Just a default test
    Author: Scala Spec
 ---------------------------------------------------
*/
package ldc.webget

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4, ScalaTestSuite}

class MySpecTest extends JUnit4(MySpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object MySpecRunner extends ConsoleRunner(MySpec)

object MySpec extends Specification {
    //Class.forName("org.hsqldb.jdbcDriver")
    "This wonderful system" should {
    "save the world" in {
      val list = Nil
      list must beEmpty
    }
  }
}
