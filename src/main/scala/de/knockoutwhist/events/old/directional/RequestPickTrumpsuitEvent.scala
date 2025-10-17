package de.knockoutwhist.events.old.directional

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.utils.events.SimpleEvent

import scala.util.Try

case class RequestPickTrumpsuitEvent(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean, player: AbstractPlayer) extends SimpleEvent {
  override def id: String = "RequestPickTrumpsuitEvent"
}
