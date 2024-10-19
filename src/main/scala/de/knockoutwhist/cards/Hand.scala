package de.knockoutwhist.cards

case class Hand(cards: List[Card]) {
  

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

}
