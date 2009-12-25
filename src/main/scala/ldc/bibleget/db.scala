/*
 * db.scala
 *
 * By Luke Harvey
 *  Data persistance class
    db class is called directly
 */

package ldc.bibleget
import scala.io.BytePickle
import java.io.File

object db {
    private val setting = new Settings
    def init = {

        //TODO some save and load code
        val seeF = new File("settings.bin")
        if (seeF.exists){
            //Load file
        }


    }

    def saveTarget (target: String) = {
        setting.setTarget(target)
    }

    def saveList (list: javax.swing.DefaultListModel) = {
        setting.setList(list)
    }

    
}
