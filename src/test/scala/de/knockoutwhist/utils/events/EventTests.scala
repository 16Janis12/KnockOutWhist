package de.knockoutwhist.utils.events

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EventTests extends AnyWordSpec with Matchers {

  "An EventListener Priority" should {
    "have a working compare too" in {
      val event1 = Priority.High
      val event2 = Priority.Normal
      val event3 = Priority.Low
      event1 should be > event2
      event2 should be < event1
      event2 should be > event3
      event3 should be < event2
      event1 should be > event3
      event3 should be < event1
    }
  }

  "The event handler" should {
    "add a listener" in {
      val eventHandler = new EventHandler() {}
      val listener = new EventListener {
        override def listen(event: SimpleEvent): Unit = {}
      }
      eventHandler.addListener(listener)
      eventHandler.listeners should contain(listener)
    }
    "remove a listener" in {
      val eventHandler = new EventHandler() {}
      val listener = new EventListener {
        override def listen(event: SimpleEvent): Unit = {}
      }
      eventHandler.addListener(listener)
      eventHandler.removeListener(listener)
      eventHandler.listeners should not contain listener
    }
    "throw an exception if an event is sent without anyone to listen" in {
      val eventHandler = new EventHandler() {}
      assertThrows[IllegalStateException] {
        eventHandler.invoke(new SimpleEvent {
          override def id: String = "testEvent"
        })
      }
    }
  }

}
