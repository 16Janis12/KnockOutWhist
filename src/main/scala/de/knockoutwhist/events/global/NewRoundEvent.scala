package de.knockoutwhist.events.global

import de.knockoutwhist.utils.events.SimpleEvent

case class NewRoundEvent() extends SimpleEvent {
  override def id: String = s"NewRoundEvent"
}
