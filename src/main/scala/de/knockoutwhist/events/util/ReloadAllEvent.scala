package de.knockoutwhist.events.util

import de.knockoutwhist.utils.events.SimpleEvent

case class ReloadAllEvent() extends SimpleEvent {
  override def id: String = "ReloadAllEvent"
}
