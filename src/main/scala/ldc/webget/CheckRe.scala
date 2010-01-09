/*
 * CheckRe.scala
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ldc.webget

import java.util.Timer
import java.util.TimerTask

class CheckRe extends TimerTask {
        def run ={
            DownController.checkDist
        }
}
