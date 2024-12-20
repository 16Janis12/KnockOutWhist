package de.knockoutwhist.cards

trait CardManager {

  def cardContainer: List[Card]

  def shuffleAndReset(): Unit

  def resetOrder(): Unit

  def nextCard(): Card

  def createHand(amount: Int = 7): Hand
  
}
