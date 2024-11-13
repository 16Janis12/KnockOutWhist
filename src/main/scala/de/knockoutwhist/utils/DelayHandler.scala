package de.knockoutwhist.utils

import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.utils.events.{EventHandler, EventListener, ReturnableEvent}

object DelayHandler extends EventListener {

  override def listen[R](event: ReturnableEvent[R]): Option[R] = {
    event match {
      case event: DelayEvent =>
        Thread.sleep(event.delay)
        Some(true)
    }
  }
}
