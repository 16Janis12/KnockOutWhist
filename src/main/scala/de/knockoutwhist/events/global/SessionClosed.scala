package de.knockoutwhist.events.global

import de.knockoutwhist.utils.events.SimpleEvent

case class SessionClosed() extends SimpleEvent {
  override def id: String = s"SessionClosed"
}
