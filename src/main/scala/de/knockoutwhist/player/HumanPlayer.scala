package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.rounds.{Round, Trick}

import scala.util.Try


class HumanPlayer private[player](name: String, hand: Option[Hand], doglife: Boolean = false) extends AbstractPlayer(name, hand, doglife) {
  override def provideHand(hand: Hand): AbstractPlayer = {
    HumanPlayer(name, Some(hand), doglife)
  }

  override def setDogLife(): AbstractPlayer = HumanPlayer(name, hand, true)

  override def removeCard(card: Card): AbstractPlayer = {
    HumanPlayer(name, Some(hand.get.removeCard(card)), doglife)
  }
  
  override def handlePlayCard(hand: Hand, round: Round, trick: Trick): Try[Card] = {
    ControlHandler.invoke(RequestCardEvent(hand))
  }

  override def handleDogPlayCard(hand: Hand, round: Round, trick: Trick, needstoplay: Boolean): Try[Option[Card]] = {
    ControlHandler.invoke(RequestDogPlayCardEvent(hand, needstoplay))
  }

  override def handlePickTrumpsuit(): Try[Suit] = {
    ControlHandler.invoke(RequestPickTrumpsuitEvent())
  }

  override def handlePickTieCard(min: Int, max: Int): Try[Int] = {
    ControlHandler.invoke(RequestNumberEvent(min, max))
  }
  
}
