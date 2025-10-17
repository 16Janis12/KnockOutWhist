package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.controllerBaseImpl.GameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.player.AbstractPlayer

final class PlayerTieLogic(gameLogic: GameLogic) {
  
  private var roundResult: Option[RoundResult] = None
  private var tiedPlayers: List[AbstractPlayer] = Nil
  private var tieBreakerIndex: Int = -1
  private var lastNumber = -1
  private var selectedCard: Map[AbstractPlayer, Card] = Map.empty
  
  def handleTie(roundResult: RoundResult): Unit = {
    this.roundResult = Some(roundResult)
    tiedPlayers = roundResult.winners
    tieBreakerIndex = 0
    lastNumber = 0
    selectedCard = Map.empty
    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    gameLogic.cardManager.get.shuffleAndReset()
    handleNextTieBreakerPlayer()
  }
  
  private def handleNextTieBreakerPlayer(): Unit = {
    if(tieBreakerIndex >= 0 && tieBreakerIndex < tiedPlayers.size) {
      val player = tiedPlayers(tieBreakerIndex)
      tieBreakerIndex += 1
      // TODO Inform observer about next player to select tie-breaker card
      // TODO Request player to select a tie-breaker card
    } else {
      // All players have selected their tie-breaker cards
      // Find the highest card among selected cards
      //TODO Inform observer about selected tie-breaker cards
      //TODO Delay for a moment to let players see the selected cards
      
      val winningEntry = selectedCard.values.maxBy(_.cardValue.ordinal)
      val winners = selectedCard.filter((_, card) => card == winningEntry).keySet
      
      if (winners.size > 1) {
        //TODO Show tie-breaker tie result to players
        //TODO Delay for a moment to let players see the tie result
        
        // Still a tie, handle again
        tiedPlayers = winners.toList
        tieBreakerIndex = 0
        lastNumber = 0
        selectedCard = Map.empty
        gameLogic.cardManager.get.shuffleAndReset()
        handleNextTieBreakerPlayer()
        return 
      }

      //TODO Show tie-breaker result to players
      
      
      // Tie-breaker resolved
      roundResult = None
      tiedPlayers = Nil
      lastNumber = -1
      tieBreakerIndex = -1
      selectedCard = Map.empty
      
      val winner = winners.head
      // Inform game logic about the winner
      
    }
  }

  /**
   * Called when a player has selected a tie-breaker card
   * @param player the player who selected the card
   * @param number the index of the selected card
   */
  def receivedTieBreakerCard(player: AbstractPlayer, number: Int): Unit = {
    val highestNumber = highestAllowedNumber()
    if (number < 0 || number > highestNumber)
      throw new IllegalArgumentException(s"Selected number $number is out of allowed range (1 to $highestNumber)")

    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    val cardManager = gameLogic.cardManager.get
    val card = cardManager.removeCards(number).last
    selectedCard += (player -> card)
    handleNextTieBreakerPlayer()
  }
  
  def highestAllowedNumber(): Int = {
    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    val remainingCards = gameLogic.cardManager.get.remainingCards
    
    // The highest allowed number is total cards minus the number of tied players already selected
    // This ensures that each tied player can select a unique card
    remainingCards - (tiedPlayers.size - selectedCard.size - 1)
  }

}
