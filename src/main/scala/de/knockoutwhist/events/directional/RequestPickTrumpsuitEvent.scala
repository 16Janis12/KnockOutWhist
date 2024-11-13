package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.utils.events.ReturnableEvent

import scala.util.Try

case class RequestPickTrumpsuitEvent() extends ReturnableEvent[Try[Suit]] {
  override def id: String = "RequestPickTrumpsuitEvent"
}
