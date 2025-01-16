package de.knockoutwhist.components

import com.google.inject.Guice
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.*
import de.knockoutwhist.control.controllerBaseImpl.*
import de.knockoutwhist.di.KnockOutLogicModule
import de.knockoutwhist.persistence.PersistenceManager
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.baseQueue.{CustomPlayerBaseQueue, CustomPlayerQueueBuilder, QueueBuilder}

object DefaultConfiguration extends Configuration {

  private val injector = Guice.createInjector(KnockOutLogicModule())

  def maincomponent: Maincomponent = injector.getInstance(classOf[Maincomponent])
  def matchcomponent: Matchcomponent = injector.getInstance(classOf[Matchcomponent])
  def playeractrcomponent: Playeractrcomponent = injector.getInstance(classOf[Playeractrcomponent])
  def playerlogcomponent: Playerlogcomponent = injector.getInstance(classOf[Playerlogcomponent])
  def roundlogcomponent: Roundlogcomponent = injector.getInstance(classOf[Roundlogcomponent])
  def trickcomponent: Tricklogcomponent = injector.getInstance(classOf[Tricklogcomponent])
  def cardManager: CardManager = injector.getInstance(classOf[CardManager])
  def persistenceManager: PersistenceManager = injector.getInstance(classOf[PersistenceManager])

  override def createRightQueue(players: Array[AbstractPlayer], start: Int): CustomPlayerQueue[AbstractPlayer] = {
    val builder = injector.getInstance(classOf[QueueBuilder])
    builder.setStart(start)
    builder.setPlayer(players)
    builder.build()
  }
}
