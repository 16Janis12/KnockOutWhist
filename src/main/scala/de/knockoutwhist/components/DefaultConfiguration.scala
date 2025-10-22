package de.knockoutwhist.components

import com.google.inject.Guice
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.di.{KnockOutConfigurationModule, KnockOutLogicModule}
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.ui.UI
import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.baseQueue.QueueBuilder
import de.knockoutwhist.utils.events.EventListener

import scala.language.postfixOps

class DefaultConfiguration extends Configuration {

  private val injector = Guice.createInjector(KnockOutLogicModule(), KnockOutConfigurationModule())
  
  override def cardManager: CardManager = injector.getInstance(classOf[CardManager])
  override def fileFormatter: FileFormatter = injector.getInstance(classOf[FileFormatter])
  override def uis: Set[UI] = Set[UI](
    TUIMain(),
    GUIMain()
  )
  override def listener: Set[EventListener] = Set[EventListener](
    utils.DelayHandler
  )

  override def createRightQueue(players: Array[AbstractPlayer], start: Int): CustomPlayerQueue[AbstractPlayer] = {
    val builder = injector.getInstance(classOf[QueueBuilder])
    builder.setStart(start)
    builder.setPlayer(players)
    builder.build()
  }
}
