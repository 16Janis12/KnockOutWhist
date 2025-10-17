package de.knockoutwhist.player

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.cards.Suit.Spades
import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.control.controllerBaseImpl.{PlayerLogic, TrickLogic}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try

class StubPlayer private[player](name: String, id: UUID = UUID.randomUUID()) extends AbstractPlayer(name, id) {

  override def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit = {
    KnockOutWhist.config.trickcomponent.controlSuitplayed(Try{Card(Ten, Spades)}, matchImpl, round, trick, currentIndex, this)
  }

  override def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit = {
    KnockOutWhist.config.trickcomponent.controlDogPlayed(Try{None}, matchImpl, round, trick, currentIndex, this)
  }

  override def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit = {
    KnockOutWhist.config.playerlogcomponent.trumpSuitSelected(matchImpl, Try{Spades}, remaining_players, firstRound, this)
  }

  override def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    KnockOutWhist.config.playerlogcomponent.selectedTie(winner, matchImpl, round, playersout, cut, Try{1}, currentStep, remaining, currentIndex)
  }
}
