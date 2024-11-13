package de.knockoutwhist.utils.events

import scala.collection.immutable
import scala.collection.immutable.TreeSet

abstract class EventHandler {
  private var listeners: TreeSet[EventListener] = immutable.TreeSet[EventListener]()

  def addListener(listener: EventListener): Int = {
    listeners = listeners + listener
    listeners.size
  }

  def removeListener(listener: EventListener): Int = {
    listeners = listeners - listener
    listeners.size
  }

  def invoke[R](event: ReturnableEvent[R]): R = {
    event match {
      case returnableEvent: ReturnableEvent[R] => {
        val result = listeners.view.map(_.listen(returnableEvent)).filter(_.isDefined).find(_.isDefined).flatten
        result.getOrElse(throw new IllegalStateException("No listener returned a result"))
      }
      case simpleEvent: SimpleEvent => {
        listeners.map(_.listen(simpleEvent)).filter(_.isDefined).map(_.get).reduce((a, b) => a && b)
      }
      case _ => throw new IllegalArgumentException("Event is not returnable")
    }
  }
  
}
