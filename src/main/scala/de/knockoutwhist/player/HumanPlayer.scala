package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestPickTrumpsuitEvent, RequestTieNumberEvent}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.collection.immutable
import scala.util.Try


class HumanPlayer private[player](name: String, hand: Option[Hand], doglife: Boolean = false) extends AbstractPlayer(name, hand, doglife) {
  override def provideHand(hand: Hand): AbstractPlayer = {
    HumanPlayer(name, Some(hand), doglife)
  }

  override def setDogLife(): AbstractPlayer = HumanPlayer(name, hand, true)

  override def removeCard(card: Card): AbstractPlayer = {
    HumanPlayer(name, Some(hand.get.removeCard(card)), doglife)
  }
  
  override def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.invoke(RequestCardEvent(hand, matchImpl, round, trick, currentIndex, this))
  }

  override def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit = {
    ControlHandler.invoke(RequestDogPlayCardEvent(hand, matchImpl, round, trick, currentIndex, this, needstoplay))
  }

  override def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit = {
    ControlHandler.invoke(RequestPickTrumpsuitEvent(matchImpl, remaining_players, firstRound, this))
  }

  override def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    ControlHandler.invoke(RequestTieNumberEvent(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex))
  }
  
}
