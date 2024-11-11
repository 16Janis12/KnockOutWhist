package de.knockoutwhist.utils.events

import scala.collection.immutable
import scala.collection.immutable.HashSet

class EventHandler {
  private var listeners: HashSet[EventListener] = immutable.HashSet[EventListener]()

  def addListener(listener: EventListener): Int = {
    listeners = listeners + listener
    listeners.size
  }

  def removeListener(listener: EventListener): Int = {
    listeners = listeners - listener
    listeners.size
  }

  def invoke(event: Event): Boolean = {
    listeners.map(_.listen(event)).reduce(_ && _)
  }
  
}
