package de.knockoutwhist.cards

import scala.collection.mutable.ListBuffer
import scala.util.Random

object CardManager {


  var cardContainer: List[Card] = {
    val cc = ListBuffer[Card]()
    for (suit <- Suit.values) {
      for (cardValue <- CardValue.values) {
        cc += Card(cardValue, suit)
      }
    }
    cc.toList
  }
  private var currentIdx = 0

  def shuffleAndReset(): Unit = {
    cardContainer = Random.shuffle(cardContainer)
    currentIdx = 0
  }

  def nextCard(): Card = {
    val card = cardContainer(currentIdx)
    currentIdx += 1
    card
  }
  
  def createHand(amount: Int = 7): Hand = {
    val hand = ListBuffer[Card]()
    for (_ <- 1 to amount) {
      hand += nextCard()
    }
    Hand(hand.toList)
  }

}
