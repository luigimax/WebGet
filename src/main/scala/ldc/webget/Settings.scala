/*
 * Settings.scala
 *
 * By Luke Harvey
 * 
 */

package ldc.webget

import scala.collection.mutable.{HashMap, HashSet, Queue}

class Settings extends java.io.Serializable {

    var target = ""
    var list = new javax.swing.DefaultListModel

    def getTarget : String ={return target}

    def setTarget(loc: String) = {target = loc}
    
    def getList : javax.swing.DefaultListModel = {return list}

    def setList(lst: javax.swing.DefaultListModel) = { list = lst}
}
