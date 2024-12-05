package de.knockoutwhist.player
import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.AILogic
import de.knockoutwhist.rounds.Trick

import scala.util.Try
  
class AIPlayer private[player](name: String, hand: Option[Hand]) extends AbstractPlayer(name, hand) {
  override def provideHand(hand: Hand): AbstractPlayer = {
    AIPlayer(name, Some(hand))
  }

  override def removeCard(card: Card): (Int, AbstractPlayer) = {
    val npl = AIPlayer(name, Some(hand.get.removeCard(card)))
    (npl.hand.size, npl)
  }
  
  override def handlePlayCard(hand: Hand, trick: Trick): Try[Card] = {
    Try{
      AILogic.decideCard(this, trick)
    }
  }

  override def handleDogPlayCard(hand: Hand, trick: Trick, needstoplay: Boolean): Try[Option[Card]] = {
    Try{
      AILogic.decideDogCard(this, trick, needstoplay)
    }
  }
  override def handlePickTrumpsuit(): Try[Suit] = {
    Try{
      AILogic.decideTrumpSuit(this)
    }
  }

  override def handlePickTieCard(min: Int, max: Int): Try[Int] = {
    Try{
      AILogic.decideTie(min, max)
    }
  }
}
