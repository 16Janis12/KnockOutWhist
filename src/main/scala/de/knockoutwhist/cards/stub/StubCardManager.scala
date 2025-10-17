package de.knockoutwhist.cards.stub

import de.knockoutwhist.cards.*

object StubCardManager extends CardManager {
  override def cardContainer: List[Card] = List()

  override def shuffleAndReset(): Unit = {}

  override def resetOrder(): Unit = {}

  override def nextCard(): Card = Card(CardValue.Ace, Suit.Clubs)
  
  override def removeCards(amount: Int): List[Card] = List(Card(CardValue.Ace, Suit.Clubs))

  override def createHand(amount: Int): Hand = Hand(List(Card(CardValue.Ace, Suit.Clubs)))

  override def grabSpecificCard(card: Card): Card = card

  override def currentIndx: Int = -1

  override def setState(cc: List[Card], currentIndex: Int): Unit = {}
}
