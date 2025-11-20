package de.knockoutwhist.utils

import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}

object DelayHandler extends EventListener {

  private[knockoutwhist] var activateDelay: Boolean = true

  override def listen(event: SimpleEvent): Unit = {
    event match {
      case event: DelayEvent =>
        if(activateDelay) {
          println(s"Delaying for ${event.delay}ms")
          Thread.sleep(event.delay)
        }
      case _ =>
    }
  }
}
