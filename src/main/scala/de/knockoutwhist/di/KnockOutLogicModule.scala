package de.knockoutwhist.di

import com.google.inject.AbstractModule
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.{Configuration, DefaultConfiguration}
import de.knockoutwhist.persistence.formats.{FileFormatter, JSONFormatter}
import de.knockoutwhist.utils.baseQueue.{CustomPlayerQueueBuilder, QueueBuilder}

class KnockOutLogicModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[CardManager]).to(classOf[CardBaseManager])
    bind(classOf[QueueBuilder]).to(classOf[CustomPlayerQueueBuilder])
    bind(classOf[FileFormatter]).to(classOf[JSONFormatter])
  }
}

class KnockOutConfigurationModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[Configuration]).to(classOf[DefaultConfiguration])
  }
  
}
