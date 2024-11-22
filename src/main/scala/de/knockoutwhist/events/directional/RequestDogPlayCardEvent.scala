package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.utils.events.ReturnableEvent

import scala.util.Try

case class RequestDogPlayCardEvent(hand: Hand, needstoplay: Boolean) extends ReturnableEvent[Try[Option[Card]]] {
  override def id: String = "RequestDogPlayCardEvent"
} 
