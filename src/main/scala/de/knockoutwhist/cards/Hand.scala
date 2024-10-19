package de.knockoutwhist.cards

case class Hand(cards: List[Card]) {

  def addCard(card: Card): Hand = {
    Hand(cards :+ card)
  }

  def removeCard(card: Card): Hand = {
    Hand(cards.filterNot(_ == card))
  }

  def removeCards(cardsToRemove: List[Card]): Hand = {
    Hand(cards.filterNot(cardsToRemove.contains))
  }

  def hasCard(card: Card): Boolean = {
    cards.contains(card)
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