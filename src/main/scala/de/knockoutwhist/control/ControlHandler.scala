package de.knockoutwhist.control

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.controllerBaseImpl.{MainLogic, MatchLogic, PlayerControl, PlayerLogic, RoundLogic, TrickLogic}
import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.{CustomThread, DelayHandler}
import de.knockoutwhist.utils.events.EventHandler

object ControlHandler extends EventHandler {

  addListener(GUIMain)
  addListener(TUIMain)
  addListener(DelayHandler)


  val maincomponent: Maincomponent = MainLogic
  val matchcomponent: Matchcomponent = MatchLogic
  val playeractrcomponent: Playeractrcomponent = PlayerControl
  val playerlogcomponent: Playerlogcomponent = PlayerLogic
  val roundlogcomponent: Roundlogcomponent = RoundLogic
  val trickcomponent: Tricklogcomponent = TrickLogic
  val cardManager: CardManager = CardBaseManager
}

object ControlThread extends CustomThread {
  
  setName("ControlThread")
  
  def isControlThread: Boolean = Thread.currentThread().equals(ControlThread)

  override def instance: CustomThread = ControlThread
}
