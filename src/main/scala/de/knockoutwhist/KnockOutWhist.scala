package de.knockoutwhist


import com.google.inject.{Guice, Inject, Injector}
import de.knockoutwhist.components.{Configuration, DefaultConfiguration}
import de.knockoutwhist.control.{ControlHandler, ControlThread}
import de.knockoutwhist.di.KnockOutConfigurationModule
import de.knockoutwhist.events.ui.GameState.MAIN_MENU
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain


object KnockOutWhist {
  
  /*
  Debug mode:

  - Disables the random shuffle of the cards
   */
  private val injector: Injector = Guice.createInjector(KnockOutConfigurationModule())
  val config: Configuration = injector.getInstance(classOf[Configuration])
  private[knockoutwhist] var DEBUG_MODE_VAR: Boolean = false

  def debugmode: Boolean = DEBUG_MODE_VAR

  def main(args: Array[String]): Unit = {
    ControlThread.start()
    if(!TUIMain.initial) throw new IllegalStateException("TUI could not be started.")
    if(!GUIMain.initial) throw new IllegalStateException("GUI could not be started.")
    ControlThread.runLater {
      ControlHandler.invoke(GameStateUpdateEvent(MAIN_MENU))
    }
  }
  
}