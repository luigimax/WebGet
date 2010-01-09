/*
---------------------------------------------------
dbSpec.scala:
    Purpose: Test object for the database
    Author: Luke Harvey
    Link: db.scala
 ---------------------------------------------------
*/

package ldc.webget

import org.specs._
import org.specs.runner.{ConsoleRunner, JUnit4, ScalaTestSuite}

class dbSpecTest extends JUnit4(dbSpec)
//class MySpecSuite extends ScalaTestSuite(MySpec)
object dbSpecRunner extends ConsoleRunner(dbSpec)

object dbSpec extends Specification {
    /*
===================================================
Declare Section:
    Purpose: Declare functions to reduce typing
    Methods:
        -mepass: causes a test to pass
        -mefail: causes a test to fail
===================================================
    */
    def mepass ={true must beTrue}
    def mefail ={false must beTrue}

    /*
===================================================
Clear Section:
    Purpose: Tests the database clear functions
===================================================
    */
    "db.clear" should{
        //Class.forName("org.hsqldb.jdbcDriver")
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
    /*
===================================================
Central Queue Section:
    Purpose: Tests the database centQueue manipulation functions
===================================================
    */
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
    /*
===================================================
Central Img Section:
    Purpose: Tests the database imgtQueue manipulation functions
===================================================
    */
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
    /*
===================================================
Done Section:
    Purpose: Tests the database Done manipulation functions
===================================================
    */
    "Done Calls should throw an exception if there is 2 of a kind" in{
        db.clear
        db.addDone("Done Calls should throw an exception if there is 2 of a kind")
            try{
                db.addDone("Done Calls should throw an exception if there is 2 of a kind")
                false must beFalse
            }catch{
                case e => true must beTrue
            }
    }
    /*
===================================================
Log Section:
    Purpose: Tests the database Log manipulation functions
===================================================
    */
    import db.{Log, LogRegex}
    "Log Calls" should {
        "Filter by State" in{
            db.clear
            db.log("debug", "Filter by state (log)", "string is cool")
            db.log("report", "Filter by state (log)", "string is cool")
            db.log("debug", "Filter by state (log)", "string is cool")
            val res = db.filterState("debug")
            var bool = false
            if(res.size == 0){
                db.log("test", "Filter by state (log)", "test returned a blank HashSet")
            }
            res.foreach((e) => {
                    e match{
                        case Log("report",_,_) =>
                            db.log("test", "Filter by state (log)", "test returned a report")
                        case Log("debug",_,_) => bool = true
                        case Log(e,_,_) =>
                            db.log("test", "Filter by state (log)", "test returned:%s".format(e))
                    }
                })
            if(bool){mepass}else mefail
        }

        "Filter by Origin" in{
            db.clear
            db.log("debug", "Filter by Origin", "string is cool")
            db.log("report", "test)", "string is cool")
            db.log("debug", "Filter by Origin", "string is cool")
            val res = db.filterOrigin("Filter by Origin")
            var bool = false
            if(res.size == 0){
                db.log("test", "Filter by Origin (log)", "test returned a blank HashSet")
            }
            res.foreach((e) => {
                    e match{
                        case Log(_,"test)",_) =>
                            db.log("test", "Filter by Origin (log)", "test returned a test)")
                        case Log(_,"Filter by Origin",_) => bool = true
                        case e =>
                            db.log("test", "Filter by Origin (log)", "test returned:%s".format(e.toString))
                    }
                })
            if(bool){mepass}else mefail
        }

        "Filter by State and Origin" in {
            db.log("debug", "Filter by State and Origin", "string is cool")
            db.log("test", "Filter by State and Origin", "string is cool")
            db.log("report", "test)", "string is cool")
            db.log("debug", "Filter by State and Origin", "string is cool")
            val res = db.filterStateOrigin("debug","Filter by State and Origin")
            var bool = true
            if(res.size == 0){
                db.log("report", "Filter by Origin (log)", "test returned a blank HashSet")
            }
            res.foreach((e) => {
                    e match{
                        case Log("debug","Filter by State and Origin",_) => 
                        case e =>
                            bool = false
                            db.log("test", "Filter by Origin (log)", "test returned:%s".format(e.toString))
                    }
                })
            if(bool){mepass}else mefail
        }

        "Regex Filter by State" in{
            db.clear
            db.log("debug", "Regex Filter by State", "string is cool")
            db.log("test", "Regex Filter by State", "string is cool")
            db.log("report", "test)", "string is cool")
            val res = db.getRegexLog(LogRegex("deb(.*)".r,"".r,"".r))
            var bool = true
            if(res.size == 0){
                db.log("report", "Regex Filter by State (log)", "test returned a blank HashSet")
            }
            if(res.size != 1){
                db.log("report", "Regex Filter by State (log)", "test returned more results that needed")
            }
            res.foreach((e) => {
                    e match{
                        case Log("debug",_,_) => println(e.toString)
                        case e =>
                            bool = false
                            db.log("test", "Filter by Origin (log)", "test returned:%s".format(e.toString))
                    }
                })
            if(bool){mepass}else mefail
        }

        "Regex Filter by Origin" in{}

        "Regex Filter by Body" in{}

        "Regex Filter by State and Origin" in{}

        "Regex Filter by State, Origin and Body" in{}

        "Return a DefaultModel of origins" in{
            db.clear
            var bool = false
            db.log("debug", "Return a DefaultModel of origins1", "string is cool")
            db.log("report", "Return a DefaultModel of origins2", "string is cool")
            db.log("debug", "Return a DefaultModel of origins3", "string is cool")
            val ret = db.getOrigins
            val ar = ret.toArray
            if(ar.size == 3) bool = true
            ar.foreach((e) => {
                    e match{
                        case "Return a DefaultModel of origins1" =>
                        case "Return a DefaultModel of origins2" =>
                        case "Return a DefaultModel of origins3" =>
                        case e =>
                            db.log("test", "Return a DefaultModel of origins (log)", "Failed result: " + e.toString)
                            bool = false
                    }
                })

            if(bool){mepass}else mefail
        }

        "Return a DefaultModel of origins filtered by State" in{}

        "Return a DefaultModel of origins filtered by origin" in{}

        "Return a DefaultModel of origins filtered by body" in{}

        "Return a DefaultModel of origins Regex filtered by State" in{}

        "Return a DefaultModel of origins Regex filtered by State" in{}

        "Return a DefaultModel of origins Regex filtered by State" in{}
    }
}