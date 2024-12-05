package de.knockoutwhist.player
import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.controlold.AILogic
import de.knockoutwhist.rounds.Trick

import scala.util.Try
  
class AIPlayer private[player](name: String, hand: Option[Hand], doglife: Boolean = false) extends AbstractPlayer(name, hand, doglife) {
  override def provideHand(hand: Hand): AbstractPlayer = {
    AIPlayer(name, Some(hand))
  }

  override def removeCard(card: Card): AbstractPlayer = {
    AIPlayer(name, Some(hand.get.removeCard(card)))
  }

  override def setDogLife(): AbstractPlayer = AIPlayer(name, hand, true)

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
