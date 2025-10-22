package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer

import scala.collection.immutable

case class Trick (cards: immutable.HashMap[Card, AbstractPlayer] = immutable.HashMap[Card, AbstractPlayer](), winner: Option[AbstractPlayer] = None, firstCard: Option[Card] = None) {

  def finished: Boolean = winner.isDefined
  
  def addCard(card: Card, player: AbstractPlayer): Trick = {
    Trick(cards + (card -> player), winner, firstCard)
  }
  
  def setfirstcard(card: Card): Trick = {
    Trick(cards, winner, Some(card))
  }

  override def toString: String = {
    s"$cards, $winner"
  }
}


