package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Card
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.Player

import scala.collection.mutable


case class Trick private(round: Round, cards: mutable.HashMap[Card, Player], winner: Player = null, finished: Boolean = false) {
  
  def this(round: Round) = {
    this(round, mutable.HashMap[Card, Player]())
  }
  private var first_card: Option[Card] = None // statt als Parameter im Konstruktor

  def get_first_card(): Option[Card] = first_card

  /**
   * Play a card in the trick
   * @param card The card to play
   * @return True if the card has a chance of winning the trick, false otherwise
   */
  def playCard(card: Card, player: Player): Boolean = {
    if (finished) {
      throw new IllegalStateException("This trick is already finished")
    } else {
      if (first_card.isEmpty) {
        first_card = Some(card)
        cards += (card -> player)
        true
      } else if (card.suit == first_card.getOrElse(card).suit) { // Wert aus Option extrahieren
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
  
  def wonTrick(): (Player, Trick) = {
    val winningCard = {
      if (cards.keys.exists(_.suit == round.trumpSuit)) {
        cards.keys.filter(_.suit == round.trumpSuit).maxBy(_.cardValue.ordinal) //stream
      } else {
        cards.keys.filter(_.suit == first_card.get.suit).maxBy(_.cardValue.ordinal) //stream
      }
    } 
    val winningPlayer = cards(winningCard)
    val finalTrick = Trick(round, cards, winningPlayer, true)
    round.tricklist += finalTrick
    (winningPlayer, finalTrick)
  }

  override def toString: String = {
    s"${round}, ${cards}, ${winner}, ${finished}"
  }
}


