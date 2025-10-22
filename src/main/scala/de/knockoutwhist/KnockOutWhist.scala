package de.knockoutwhist


import com.google.inject.{Guice, Injector}
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.di.KnockOutConfigurationModule
import de.knockoutwhist.utils.events.EventListener


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
    val baseLogic = BaseGameLogic(configuration)
    for (handler <- configuration.listener) baseLogic.addListener(handler)
    ControlThread.start()
    for (ui <- configuration.uis) {
      if (!ui.initial(baseLogic)) throw new IllegalStateException(s"${ui.getClass.getName} could not be started.")
      ui match {
        case eventListener: EventListener => baseLogic.addListener(eventListener)
        case _ =>
      }
    }
  }
  
}