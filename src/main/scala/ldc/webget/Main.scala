package ldc.webget
import scala.util.matching.Regex

object Main {
    
   def main(args:Array[String]): Unit = {
      println("Big World")
      try {
            Class.forName("org.hsqldb.jdbcDriver")
        } catch{
           case e =>
               e.printStackTrace()
               println("Unable to load Database Driver. There is an issue with the classpath")
        }
      db.init
      //val url = "http://www.onemanga.com/directory/"
      //val one = new OneManga
      //one.run
      //db.dir
      //val LogEntry = """Completed in (\d+)ms \(View: (\d+), DB: (\d+)\) \| (\d+) OK \[http://app.domain.com(.*)\?.*""".r
      //val line = "Completed in 100ms (View: 25, DB: 75) | 200 OK [http://app.domain.com?params=here]"
      //val LogEntry(totalTime, viewTime, dbTime, responseCode, uri) = line
      //println(totalTime)
      var n = 0
      println("fancy %s".format(n))
      n = n+1
      db.addImgQ("string", "string")
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println("fancy %s".format(n))
      n = n+1
      db.addImgQ("string", "string")
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println(db.getImgQ)
      println("fancy %s".format(n))
      n = n+1
      //DataController.event("report", "HTML Parser: 2", "13-ch0000-p0037.jpg already exists")
      val r1 = new MainFrm
      r1.setVisible(true)
      //fancy scala voodoo to get the update func to db class
      db.logUpdate = db.logUp(r1.logUpdate _)

      
      true
    }
}
