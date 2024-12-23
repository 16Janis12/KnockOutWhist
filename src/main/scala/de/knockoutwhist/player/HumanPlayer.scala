package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.rounds.Trick

import scala.util.Try


class HumanPlayer private[player](name: String) extends AbstractPlayer(name) {
  override def handlePlayCard(hand: Hand, trick: Trick): Try[Card] = {
    ControlHandler.invoke(RequestCardEvent(hand))
    }

  override def handleDogPlayCard(hand: Hand, trick: Trick, needstoplay: Boolean): Try[Option[Card]] = {
    ControlHandler.invoke(RequestDogPlayCardEvent(hand, needstoplay))
  }

  override def handlePickTrumpsuit(): Try[Suit] = {
    ControlHandler.invoke(RequestPickTrumpsuitEvent())
  }

  override def handlePickTieCard(min: Int, max: Int): Try[Int] = {
    ControlHandler.invoke(RequestNumberEvent(min, max))
  }
  
}
