package de.knockoutwhist.control

import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.DelayHandler
import de.knockoutwhist.utils.events.EventHandler

object ControlHandler extends EventHandler {

  addListener(GUIMain)
  //addListener(TUIMain)
  addListener(DelayHandler)

}
