package de.knockoutwhist.events.directional

import de.knockoutwhist.utils.events.ReturnableEvent

import scala.util.Try

case class RequestNumberEvent(min: Int, max: Int) extends ReturnableEvent[Try[Int]] {
  override def id: String = "RequestNumberEvent"
}
