package de.knockoutwhist.control

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Trick

import scala.compiletime.ops.float.Max
import scala.util.Random

case object AILogic {
  
  def decideCard(ai: AbstractPlayer, trick: Trick): Card = {
    val firstCardSuit = trick.getfirstcard().get.suit
    val hand = ai.currentHand().get
    val cardsOfSuit = hand.cards.filter(_.suit == firstCardSuit)
    val trumpsInGame = trick.cards.keys.filter(_.suit == trick.round.trumpSuit)
    if (cardsOfSuit.isEmpty) {
      val trumpCards = hand.cards.filter(_.suit == trick.round.trumpSuit)
      if (trumpCards.isEmpty) hand.cards.minBy(_.cardValue.ordinal)
      else {
        val bestOption = decideWhichTrumpCard(hand, trick, trumpsInGame.toList)
        bestOption match {
          case Some(card) => card
          case None => hand.cards.minBy(_.cardValue.ordinal)
        }
      }
    } else {
      if(trumpsInGame.nonEmpty) cardsOfSuit.minBy(_.cardValue.ordinal)
      else cardsOfSuit.maxBy(_.cardValue.ordinal)
    }
  }

  private def decideWhichTrumpCard(hand: Hand, trick: Trick, activeTrumps: List[Card]): Option[Card] = {
    val trumpCards = hand.cards.filter(_.suit == trick.round.trumpSuit)
    if (trick.remainingPlayers == 1 && activeTrumps.isEmpty) return Some(trumpCards.minBy(_.cardValue.ordinal))
    val highestTrump = trumpCards.maxBy(_.cardValue.ordinal)
    val activeTrump = activeTrumps.maxBy(_.cardValue.ordinal)
    if (highestTrump.cardValue.ordinal < activeTrump.cardValue.ordinal) None
    else {
      val higherTrumps = trumpCards.filter(_.cardValue.ordinal > activeTrump.cardValue.ordinal)
      if(trick.remainingPlayers <= trick.round.playersin.size * 0.5) Some(higherTrumps.minBy(_.cardValue.ordinal))
      else Some(highestTrump)
    }
  }
  
  private def decideTrumpSuit(ai: AbstractPlayer): Suit = {
    val hand = ai.currentHand().get
    hand.cards.groupBy(_.suit).maxBy(_._2.size)._1
  }
  
  private def decideTie(min: Int, max: Int): Int = {
    Random.between(min, max+1)
  }
}
