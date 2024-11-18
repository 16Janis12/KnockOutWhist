package de.knockoutwhist.events.util

import de.knockoutwhist.utils.events.SimpleEvent

case class DelayEvent (delay: Long) extends SimpleEvent {
  override def id: String = "DelayEvent"
}
