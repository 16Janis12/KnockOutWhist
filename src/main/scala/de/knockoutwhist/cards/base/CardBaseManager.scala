package de.knockoutwhist.cards.base

import de.knockoutwhist.cards.*

import scala.collection.mutable.ListBuffer
import scala.util.Random

class CardBaseManager extends CardManager {

  override def setState(cc: List[Card], currentIndex: Int): Unit = {
    this.cc = cc
    this.currentIdx = currentIndex
  }
  
  override def cardContainer: List[Card] = cc
  private var cc: List[Card] = {
    val cc = ListBuffer[Card]()
    for (suit <- Suit.values) {
      for (cardValue <- CardValue.values) {
        cc += Card(cardValue, suit)
      }
    }
    cc.toList
  }
  private var currentIdx = 0

  override def currentIndx: Int = currentIdx

  override def shuffleAndReset(): Unit = {
    cc = Random.shuffle(cc)
    currentIdx = 0
  }
  
  override def resetOrder(): Unit = {
    cc = cc.sortBy(c => (c.suit.ordinal, c.cardValue.ordinal))
    currentIdx = 0
  }

  override def nextCard(): Card = {
    if (currentIdx > 51)
      throw new IndexOutOfBoundsException("Trying to access card 53(out of bounds)")
    val card = cc(currentIdx)
    currentIdx += 1
    card
  }
  
  override def removeCards(amount: Int): List[Card] = {
    val removedCards = ListBuffer[Card]()
    for (_ <- 0 to amount) {
      removedCards += nextCard()
    }
    removedCards.toList
  }
  
  override def createHand(amount: Int = 7): Hand = {
    val hand = ListBuffer[Card]()
    for (_ <- 1 to amount) {
      hand += nextCard()
    }
    Hand(hand.toList)
  }

  override def grabSpecificCard(card: Card): Card = {
    cc.filter(c => c.suit == card.suit && c.cardValue == card.cardValue).head
  }
}
