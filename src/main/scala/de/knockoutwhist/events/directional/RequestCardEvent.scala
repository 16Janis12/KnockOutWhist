package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.utils.events.ReturnableEvent

import scala.util.Try

case class RequestCardEvent(hand: Hand) extends ReturnableEvent[Try[Card]] {
  override def id: String = "RequestCardEvent"
}
