package de.knockoutwhist

import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.utils.DelayHandler
import de.knockoutwhist.utils.events.EventHandler
import org.scalatest.concurrent.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.SpanSugar.*
import org.scalatest.wordspec.AnyWordSpec

import scala.language.postfixOps

class DelayHandlerTests extends AnyWordSpec with TimeLimits with Matchers {

  private val eventHandler = new EventHandler() {}
  eventHandler.addListener(DelayHandler)

  "A delay event" should  {
    val delayEvent = DelayEvent(100)
    "be able to be created" in {
      delayEvent should not be null
    }
    "have the correct id" in {
      delayEvent.id should be ("DelayEvent")
    }
    "have the correct delay" in {
      delayEvent.delay should be (100)
    }
    "not be longer than 120ms if it's set to 100ms" in {
      DelayHandler.activateDelay = true
      failAfter(200 millis) {
        eventHandler.invoke(delayEvent)
      }
    }
  }

}
