package de.knockoutwhist.cards

import scala.collection.mutable.ListBuffer

case class Hand(cards: List[Card]) {

  def removeCard(card: Card): Hand = {
    Hand(cards.filter(_ != card))
  }

  def hasSuit(suit: Suit): Boolean = {
    cards.exists(_.suit == suit)
  }

  def hasValue(cardValue: CardValue): Boolean = {
    cards.exists(_.cardValue == cardValue)
  }

  def hasTrumpSuit(trumpSuit: Suit): Boolean = {
    cards.exists(_.suit == trumpSuit)
  }

}
