/*
 * dbSpec.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4, ScalaTestSuite}

class dbSpecTest extends JUnit4(dbSpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object dbSpecRunner extends ConsoleRunner(dbSpec)

object dbSpec extends Specification {
    
    "db.clear" should{
        Class.forName("org.hsqldb.jdbcDriver")
        "Wipe the Central Queue" in {
            db.addCentQ("Wipe the Central Queue")
            db.clear
            val r = db.getCentQ
            r match{
                case "" => true must beTrue
                case "Wipe the Central Queue" =>
                    println("returned: Wipe the Central Queue")
                    false must beTrue
                case _ => false must beTrue
            }
        }
        "Wipe the Image Queue" in {
            db.clear
            db.addImgQ("string", "string")
            db.clear
            val r = db.getImgQ
            r match{
                case Img("string","string") => false must beTrue
                case Img("empt", "empt")=> true must beTrue
                case _ => false must beTrue
            }
        }
        "Wipe the Done list" in {
            db.addDone("string is done")
            db.clear
            try{
                db.addDone("string is done")
                true must beTrue
            }catch{
                case e => false must beTrue
            }
            
        }     
    }

    "Central Queue Calls" should {
        "Add a line into the db" in{
            db.clear
            db.addCentQ("Add a line into the db")
            db.getCentQ match {
                case "Add a line into the db" =>
                    true must beTrue
                case _ => false must beFalse
            }
        }
        "Return an accurate count of database items" in {
            db.clear
            var i = 1
            while (i <= 10) {
                db.addCentQ("Count me %s".format(i))
                i = i+1
            }
            val cn = db.countCentQ
            if(cn == 10){true must beTrue}else false must beTrue
        }
        "Throw an exception if 2 of the same are in the db" in {
            db.clear
            db.addCentQ("Throw an exception if 2 of the same are in the db")
            try{
                db.addCentQ("Throw an exception if 2 of the same are in the db")
                false must beFalse
            }catch{
                case e => true must beTrue
            }
        }
        "Delete the last retrived entry (centQ)" in {
            db.clear
            db.addCentQ("Delete the last retrived entry")
            db.addCentQ("Delete the last retrived entry2")
            val res = db.getCentQ
            db.countCentQ match {
                case 1 => true must beTrue
                case 2 => false must beTrue
                case n => println(n + "delete last retrived entry");false must beTrue
            }
        }

    }

    "Central Img Queue Calls" should {
        "Add a line into the db" in{
            db.clear
            db.addImgQ("Add a line into the db", "string")
            db.getImgQ match {
                case Img("Add a line into the db", "string") =>
                    true must beTrue
                case _ => false must beFalse
            }
        }
        "Return an accurate count of database items" in {
            db.clear
            var i = 1
            while (i <= 10) {
                db.addImgQ("Count me %s".format(i), "I am counted")
                i = i+1
            }
            val cn = db.countImgQ
            if(cn == 10){true must beTrue}else false must beTrue
        }
        "Throw an exception if 2 of the same are in the db" in {
            db.clear
            db.addImgQ("Throw an exception if 2 of the same are in the db","String")
            try{
                db.addImgQ("Throw an exception if 2 of the same are in the db","String")
                false must beFalse
            }catch{
                case e => true must beTrue
            }
        }
        "Delete the last retrived entry (img)" in {
            db.clear
            db.addImgQ("Delete the last retrived entry","Delete the last retrived entry")
            db.addImgQ("Delete the last retrived entry2","Delete the last retrived entry2")
            val res = db.getImgQ
            val nc = db.countImgQ
            nc match {
                case 1 => true must beTrue
                case 2 => false must beTrue
                case n => 
                    println(n + "delete last retrived entry img")
                    false must beTrue
            }
        }
    }

    "Done Calls should throw an exception if there is 2 of a kind" in{
        db.clear
        
    }
}
