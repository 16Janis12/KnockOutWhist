package de.knockoutwhist.cards.base

import de.knockoutwhist.cards.*

import scala.collection.mutable.ListBuffer
import scala.util.Random

object CardBaseManager extends CardManager {
  
  
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

  override def shuffleAndReset(): Unit = {
    cc = Random.shuffle(cc)
    currentIdx = 0
  }
  
  override def resetOrder(): Unit = {
    cc = cc.sortBy(c => (c.suit.ordinal, c.cardValue.ordinal))
    currentIdx = 0
  }

  override def nextCard(): Card = {
    val card = cc(currentIdx)
    if (currentIdx + 1 > 51) {
      throw new IndexOutOfBoundsException("Trying to access card 53(out of bounds)")
    } else {
      currentIdx += 1
      card
    }
  }
  
  override def createHand(amount: Int = 7): Hand = {
    val hand = ListBuffer[Card]()
    for (_ <- 1 to amount) {
      hand += nextCard()
    }
    Hand(hand.toList)
  }

  class CardManagerSnapshot(val cc: List[Card], val currentIdx: Int) {

    def restore(): Unit = {
      CardBaseManager.cc = cc
      CardBaseManager.currentIdx = currentIdx
    }

  }

  object CardManagerSnapshot {
    def apply(): CardManagerSnapshot = new CardManagerSnapshot(CardBaseManager.cc, CardBaseManager.currentIdx)
  }

}
