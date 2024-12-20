package de.knockoutwhist.cards.mock

import de.knockoutwhist.cards.{Card, CardManager, CardValue, Hand, Suit}

object MockCardManager extends CardManager {
  override def cardContainer: List[Card] = List()

  override def shuffleAndReset(): Unit = {}

  override def resetOrder(): Unit = {}

  override def nextCard(): Card = Card(CardValue.Ace, Suit.Clubs)

  override def createHand(amount: Int): Hand = Hand(List(Card(CardValue.Ace, Suit.Clubs)))
  
}
