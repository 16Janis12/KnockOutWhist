package de.knockoutwhist.cards

import scala.util.Random

object CardManager {


  val cardContainer: Array[Card] = {
    val cc = new Array[Card](52)
    var i = 0
    for (suit <- Suit.values) {
      for (cardValue <- CardValue.values) {
        cc(i) = Card(cardValue, suit)
        i+=1
      }
    }
    cc
  }
  private var currentIdx = 0

  def shuffleAndReset(): Unit = {
    Random.shuffle(cardContainer)
    currentIdx = 0
  }

  def nextCard(): Card = {
    val card = cardContainer(currentIdx)
    currentIdx += 1
    card
  }



}
