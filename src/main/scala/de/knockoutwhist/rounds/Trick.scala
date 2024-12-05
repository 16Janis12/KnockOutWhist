package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer

import scala.collection.immutable

case class Trick (cards: immutable.HashMap[Card, AbstractPlayer] = immutable.HashMap[Card, AbstractPlayer](), winner: AbstractPlayer = null, finished: Boolean = false, firstCard: Option[Card] = None) {

  def addCard(card: Card, player: AbstractPlayer): Trick = {
    Trick(cards + (card -> player), winner, finished, firstCard)
  }
  
  def setfirstcard(card: Card): Trick = {
    Trick(cards, winner, finished, Some(card))
  }

  override def toString: String = {
    s"$cards, $winner, $finished"
  }
}


