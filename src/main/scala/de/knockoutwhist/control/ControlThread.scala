package de.knockoutwhist.control

import de.knockoutwhist.utils.CustomThread

object ControlThread extends CustomThread {
  
  setName("ControlThread")
  
  def isControlThread: Boolean = Thread.currentThread().equals(ControlThread)

  override def instance: CustomThread = ControlThread
}
