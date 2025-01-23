package de.knockoutwhist.player
import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.AILogic
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try
  
class AIPlayer private[player](name: String, hand: Option[Hand], id: UUID = UUID.randomUUID(), doglife: Boolean = false) extends AbstractPlayer(name, hand, id, doglife) {
  override def provideHand(hand: Hand): AbstractPlayer = {
    AIPlayer(name, Some(hand), id, doglife)
  }

  override def removeCard(card: Card): AbstractPlayer = {
    AIPlayer(name, Some(hand.get.removeCard(card)), id, doglife)
  }

  override def setDogLife(): AbstractPlayer = AIPlayer(name, hand, id, true)

  override def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit = {
    AILogic.decideCard(this, round, trick)
  }

  override def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit = {
    AILogic.decideDogCard(this, round, trick, needstoplay)
  }
  
  override def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit = {
    AILogic.decideTrumpSuit(this)
  }

  override def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    AILogic.decideTie(1, remaining)
  }
  
}
