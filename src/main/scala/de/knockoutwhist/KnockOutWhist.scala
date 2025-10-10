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
  Debug mode
  - Disables the random shuffle of the cards
   */
  private[knockoutwhist] var DEBUG_MODE_VAR: Boolean = false
  
  private var _config: Option[Configuration] = None
  
  def config: Configuration = _config.get

  def debugmode: Boolean = DEBUG_MODE_VAR

  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(KnockOutConfigurationModule())
    val config: Configuration = injector.getInstance(classOf[Configuration])
    entry(config)
  }
  
  def entry(configuration: Configuration): Unit = {
    _config = Some(configuration)
    for (handler <- configuration.listener) ControlHandler.addListener(handler)
    ControlThread.start()
    configuration.persistenceManager.loadManager()
    for (ui <- configuration.uis) {
      if (!ui.initial) throw new IllegalStateException(s"${ui.getClass.getName} could not be started.")
    }
    ControlThread.runLater {
      ControlHandler.invoke(GameStateUpdateEvent(MAIN_MENU))
    }
  }
  
}