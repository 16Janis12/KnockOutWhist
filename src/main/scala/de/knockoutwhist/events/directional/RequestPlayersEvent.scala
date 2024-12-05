package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.ReturnableEvent

import scala.util.Try

case class RequestPlayersEvent() extends ReturnableEvent[List[AbstractPlayer]] {
  override def id: String = "RequestPlayersEvent"
}
