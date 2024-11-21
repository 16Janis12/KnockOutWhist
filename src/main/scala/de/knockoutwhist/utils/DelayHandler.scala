package de.knockoutwhist.utils

import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent}

object DelayHandler extends EventListener {

  private[knockoutwhist] var activateDelay: Boolean = true

  override def listen[R](event: ReturnableEvent[R]): Option[R] = {
    event match {
      case event: DelayEvent =>
        if(activateDelay) Thread.sleep(event.delay)
        Some(true)
      case _ => None
    }
  }
}
