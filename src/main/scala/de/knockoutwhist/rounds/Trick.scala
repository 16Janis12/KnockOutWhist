package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Card
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.Player

import scala.collection.mutable


case class Trick (round: Round, cards: mutable.HashMap[Card, Player], winner: Player = null, finished: Boolean = false) {
  
  def this(round: Round) = {
    this(round, mutable.HashMap[Card, Player]())
  }
  private var first_card: Option[Card] = None // statt als Parameter im Konstruktor

  def set_first_card(card: Card): Option[Card] = {
    if(first_card.isDefined) {
      throw new IllegalStateException("This trick is already finished")
    }
    first_card = Some(card)
    first_card
  }
  def get_first_card(): Option[Card] = first_card

  override def toString: String = {
    s"$cards, $winner, $finished"
  }
}


