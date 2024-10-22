package de.knockoutwhist.rounds

import de.knockoutwhist.cards.{Card, Player}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Trick private(round: Round, firstCard: Card, cards: mutable.HashMap[Card, Player], winner: Player = null, finished: Boolean = false) {
  
  def this(round: Round, firstCard: Card) = this(round, firstCard, mutable.HashMap[Card, Player]())

  /**
   * Play a card in the trick
   * @param card The card to play
   * @return True if the card has a chance of winning the trick, false otherwise
   */
  def playCard(card: Card, player: Player): Boolean = {
    if (cards.isEmpty) {
      throw new IllegalStateException("Cannot play a card in an empty trick")
    } else if (trickFinished()) {
      throw new IllegalStateException("This trick is already finished")
    } else {
      if (card.suit == firstCard.suit) {
        cards += (card -> player)
        true
      } else if (card.suit == round.trumpSuit) {
        cards += (card -> player)
        true
      } else {
        cards += (card -> player)
        false
      }
    }
  }
  
  def trickFinished(): Boolean = {
    cards.size == round.cardAmount
  }
  
  def wonTrick(): (Player, Trick) = {
    if (!trickFinished()) {
      throw new IllegalStateException("Cannot determine winner of unfinished trick")
    } else {
      val winningCard = {
        if (cards.keys.exists(_.suit == round.trumpSuit)) {
          cards.keys.filter(_.suit == round.trumpSuit).maxBy(_.cardValue.ordinal)
        } else {
          cards.keys.filter(_.suit == firstCard.suit).maxBy(_.cardValue.ordinal)
        }
      }
      val winningPlayer = cards(winningCard)
      (winningPlayer, Trick(round, firstCard, cards, winningPlayer, true))
    }
  }
  
}
