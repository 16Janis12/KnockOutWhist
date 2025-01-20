package de.knockoutwhist.di

import com.google.inject.AbstractModule
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.{Configuration, DefaultConfiguration}
import de.knockoutwhist.control.controllerBaseImpl.*
import de.knockoutwhist.control.*
import de.knockoutwhist.persistence.PersistenceManager
import de.knockoutwhist.persistence.formats.{FileFormatter, JSONFormatter, XMLFormatter}
import de.knockoutwhist.persistence.stub.PersistenceBaseManager
import de.knockoutwhist.utils.baseQueue.{CustomPlayerQueueBuilder, QueueBuilder}

class KnockOutLogicModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[Maincomponent]).toInstance(MainLogic)
    bind(classOf[Matchcomponent]).toInstance(MatchLogic)
    bind(classOf[Playeractrcomponent]).toInstance(PlayerControl)
    bind(classOf[Playerlogcomponent]).toInstance(PlayerLogic)
    bind(classOf[Roundlogcomponent]).toInstance(RoundLogic)
    bind(classOf[Tricklogcomponent]).toInstance(TrickLogic)
    bind(classOf[CardManager]).to(classOf[CardBaseManager])
    bind(classOf[QueueBuilder]).to(classOf[CustomPlayerQueueBuilder])
    bind(classOf[PersistenceManager]).toInstance(PersistenceBaseManager)
    bind(classOf[FileFormatter]).to(classOf[XMLFormatter])
  }
}

class KnockOutConfigurationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Configuration]).toInstance(DefaultConfiguration)
  }
  
}