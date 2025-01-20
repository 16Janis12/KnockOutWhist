package de.knockoutwhist.utils.events

import de.knockoutwhist.control.ControlThread

import scala.collection.mutable

abstract class EventHandler {
  private[events] var listeners: mutable.ListBuffer[EventListener] = mutable.ListBuffer[EventListener]()

  def addListener(listener: EventListener): Int = {
    listeners = (listeners += listener).sorted
    listeners.size
  }

  def removeListener(listener: EventListener): Int = {
    listeners = (listeners -= listener).sorted
    listeners.size
  }

  def invoke(event: SimpleEvent): Boolean = {
    if (!ControlThread.isControlThread) {
      throw new IllegalStateException("Cannot invoke event from a non control thread")
    }
    event match {
      case simpleEvent: SimpleEvent =>
        listeners.map(_.listen(simpleEvent)).nonEmpty
    }
  }
  
}
