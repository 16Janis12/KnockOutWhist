package de.knockoutwhist.components

import com.google.inject.Guice
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.*
import de.knockoutwhist.control.controllerBaseImpl.*
import de.knockoutwhist.di.KnockOutLogicModule
import de.knockoutwhist.persistence.PersistenceManager
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.ui.UI
import de.knockoutwhist.ui.gui.GUIMain
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils
import de.knockoutwhist.utils.{CustomPlayerQueue, DelayHandler}
import de.knockoutwhist.utils.baseQueue.{CustomPlayerBaseQueue, CustomPlayerQueueBuilder, QueueBuilder}
import de.knockoutwhist.utils.events.EventListener

import scala.language.postfixOps

class DefaultConfiguration extends Configuration {

  private val injector = Guice.createInjector(KnockOutLogicModule())

  def maincomponent: Maincomponent = injector.getInstance(classOf[Maincomponent])
  def matchcomponent: Matchcomponent = injector.getInstance(classOf[Matchcomponent])
  def playeractrcomponent: Playeractrcomponent = injector.getInstance(classOf[Playeractrcomponent])
  def playerlogcomponent: Playerlogcomponent = injector.getInstance(classOf[Playerlogcomponent])
  def roundlogcomponent: Roundlogcomponent = injector.getInstance(classOf[Roundlogcomponent])
  def trickcomponent: Tricklogcomponent = injector.getInstance(classOf[Tricklogcomponent])
  def cardManager: CardManager = injector.getInstance(classOf[CardManager])
  def persistenceManager: PersistenceManager = injector.getInstance(classOf[PersistenceManager])
  def fileFormatter: FileFormatter = injector.getInstance(classOf[FileFormatter])
  def uis: Set[UI] = Set[UI](
    TUIMain,
    GUIMain
  )
  override def listener: Set[EventListener] = Set[EventListener](
    TUIMain,
    GUIMain,
    utils.DelayHandler
  )

  override def createRightQueue(players: Array[AbstractPlayer], start: Int): CustomPlayerQueue[AbstractPlayer] = {
    val builder = injector.getInstance(classOf[QueueBuilder])
    builder.setStart(start)
    builder.setPlayer(players)
    builder.build()
  }
}
