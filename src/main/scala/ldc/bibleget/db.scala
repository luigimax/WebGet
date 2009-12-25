/*
 * db.scala
 *
 * By Luke Harvey
 * 
 */

package ldc.bibleget

import java.sql.DriverManager
import org.hsqldb.jdbc._

object db {

    var c: java.sql.Connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb", "SA", "")

    def conn ={ 
        //c
    }

    def kill = {
        //c.nativeSQL("SHUTDOWN")
    }

    def dir = {
        //c.nativeSQL("create table dir(loc blob)")
        //c.nativeSQL("")
    }
}
