package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

import scala.util.Try

case class RequestPlayersEvent() extends SimpleEvent {
  override def id: String = "RequestPlayersEvent"
}
