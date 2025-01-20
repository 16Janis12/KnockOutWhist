package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.events.SimpleEvent

import scala.util.Try

case class RequestDogPlayCardEvent(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer, needstoplay: Boolean) extends SimpleEvent {
  override def id: String = "RequestDogPlayCardEvent"
} 
