package de.knockoutwhist.utils.events

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

  def invoke[R](event: ReturnableEvent[R]): R = {
    event match {
      case simpleEvent: SimpleEvent =>
        val result = listeners.map(_.listen(simpleEvent)).filter(_.isDefined).map(_.get).toList
        if(result.isEmpty) return false
        result.reduce((a,b) => a && b)
      case returnableEvent: ReturnableEvent[R] =>
        val result = listeners.view.map(_.listen(returnableEvent)).find(_.isDefined).flatten
        result.getOrElse(throw new IllegalStateException("No listener returned a result"))
    }
  }
  
}
