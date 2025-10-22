package de.knockoutwhist.events.global

import de.knockoutwhist.utils.events.SimpleEvent

case class NewTrickEvent() extends SimpleEvent {
  override def id: String = s"NewTrickEvent"
}
