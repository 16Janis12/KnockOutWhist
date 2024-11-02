package de.knockoutwhist.cards

import scala.collection.mutable.ListBuffer

case class Hand(cards: ListBuffer[Card]) {

  def removeCard(card: Card): Hand = {
    Hand(cards.filter(_ != card))
    //Hand(cards.filterNot(_ == card)) isch wurscht welches wir nehmen
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
  
  def renderAsString() : ListBuffer[String] = {
    val cardStrings = cards.map(_.renderAsString())
    val zipped = cardStrings.transpose
    zipped.map(_.mkString(" "))
  }

}
