package de.knockoutwhist.cards

trait CardManager {

  def cardContainer: List[Card]

  def shuffleAndReset(): Unit

  def resetOrder(): Unit

  def nextCard(): Card
  
  def remainingCards: Int = cardContainer.size - currentIndx

  def removeCards(amount: Int): List[Card]
  
  def createHand(amount: Int = 7): Hand
  
  def grabSpecificCard(card: Card): Card
  
  def currentIndx: Int

  def setState(cc: List[Card], currentIndex: Int): Unit
  
}
