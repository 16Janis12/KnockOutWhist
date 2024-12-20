package de.knockoutwhist.cards

trait CardManager {
  /**
   * The container of cards, where all cards are stored
   * @return List[Card]
   */
  def cardContainer: List[Card]
  /**
   * Shuffles the card container and resets the current index
   */
  def shuffleAndReset(): Unit
  /**
   * Resets the order of the card container and resets the current index
   */
  def resetOrder(): Unit
  /**
   * Returns the next card in the card container
   * @return Card
   */
  def nextCard(): Card
  /**
   * Creates a hand of cards
   * @param amount The amount of cards in the hand
   * @return Hand
   */
  def createHand(amount: Int = 7): Hand
  
}
