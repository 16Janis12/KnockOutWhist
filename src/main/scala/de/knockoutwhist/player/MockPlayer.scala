package de.knockoutwhist.player

import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.cards.Suit.Spades
import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.control.controllerBaseImpl.{PlayerLogic, TrickLogic}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.collection.immutable
import scala.util.Try

class MockPlayer private[player](name: String, hand: Option[Hand], doglife: Boolean = false) extends AbstractPlayer(name, hand, doglife) {
  
  override def provideHand(hand: Hand): AbstractPlayer = {
    MockPlayer(name, Some(hand), doglife)
  }

  override def removeCard(card: Card): AbstractPlayer = {
    MockPlayer(name, Some(hand.get.removeCard(card)), doglife)
  }

  override def setDogLife(): AbstractPlayer = MockPlayer(name, hand, true)

  override def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.trickcomponent.controlSuitplayed(Try{Card(Ten, Spades)}, matchImpl, round, trick, currentIndex, this)
  }

  override def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit = {
    ControlHandler.trickcomponent.controlDogPlayed(Try{None}, matchImpl, round, trick, currentIndex, this)
  }

  override def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit = {
    ControlHandler.playerlogcomponent.trumpSuitSelected(matchImpl, Try{Spades}, remaining_players, firstRound, this)
  }

  override def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    ControlHandler.playerlogcomponent.selectedTie(winner, matchImpl, round, playersout, cut, Try{1}, currentStep, remaining, currentIndex)
  }
}
