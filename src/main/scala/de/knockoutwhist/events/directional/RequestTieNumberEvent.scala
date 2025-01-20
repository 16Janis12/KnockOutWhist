package de.knockoutwhist.events.directional

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.utils.events.SimpleEvent

import scala.collection.immutable
import scala.util.Try

case class RequestTieNumberEvent(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0) extends SimpleEvent {
  override def id: String = "RequestNumberEvent"
}
