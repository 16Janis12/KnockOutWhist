package de.knockoutwhist.control

import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.events.EventHandler
import de.knockoutwhist.utils.{CustomThread, DelayHandler}

object ControlHandler extends EventHandler {

  addListener(GUIMain)
  addListener(TUIMain)
  addListener(DelayHandler)
  
}

object ControlThread extends CustomThread {
  
  setName("ControlThread")
  
  def isControlThread: Boolean = Thread.currentThread().equals(ControlThread)

  override def instance: CustomThread = ControlThread
}
