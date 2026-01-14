package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.LogicSnapshot
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{ResultPlayer, RoundResult}
import de.knockoutwhist.control.sublogic.PlayerTieLogic
import de.knockoutwhist.events.global.tie.*
import de.knockoutwhist.events.player.RequestTieChoiceEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.AbstractPlayer

final class BasePlayerTieLogic(gameLogic: BaseGameLogic) extends PlayerTieLogic {
  
  private[control] var roundResult: Option[RoundResult] = None
  private[control] var tiedPlayers: List[AbstractPlayer] = Nil
  private[control] var tieBreakerIndex: Int = -1
  private[control] var lastNumber = -1
  private[control] var selectedCard: Map[AbstractPlayer, Card] = Map.empty
  private var _waitingForInput: Boolean = false

  override def handleTie(roundResult: RoundResult): Unit = {
    this.roundResult = Some(roundResult)
    tiedPlayers = roundResult.winners
    tieBreakerIndex = -1
    lastNumber = 0
    selectedCard = Map.empty
    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    gameLogic.cardManager.get.shuffleAndReset()
    handleNextTieBreakerPlayer()
  }
  
  override def handleNextTieBreakerPlayer(): Unit = {
    tieBreakerIndex += 1
    if(tieBreakerIndex >= 0 && tieBreakerIndex < tiedPlayers.size) {
      requestTieChoice(currentTiePlayer().get)
    } else {
      // All players have selected their tie-breaker cards
      // Find the highest card among selected cards
      
      gameLogic.invoke(TieAllPlayersSelectedEvent())
      gameLogic.invoke(DelayEvent(200))
      gameLogic.invoke(TieShowPlayerCardsEvent())
      gameLogic.invoke(DelayEvent(2000))
      
      val winningEntry = selectedCard.values.maxBy(_.cardValue.ordinal)
      val winners = selectedCard.filter((_, card) => card == winningEntry).keySet.toList

      gameLogic.invoke(TieWinningPlayersEvent(winners))
      gameLogic.invoke(DelayEvent(2000))
      if (winners.size > 1) {
        gameLogic.invoke(TieTieEvent(winners))
        
        // Still a tie, handle again
        tiedPlayers = winners
        tieBreakerIndex = -1
        lastNumber = 0
        selectedCard = Map.empty
        gameLogic.cardManager.get.shuffleAndReset()
        handleNextTieBreakerPlayer()
        return 
      }
      
      
      // Tie-breaker resolved
      roundResult = None
      tiedPlayers = Nil
      lastNumber = -1
      tieBreakerIndex = -1
      selectedCard = Map.empty
      
      val winner = winners.head
      // Inform game logic about the winner
      gameLogic.returnFromTie(winner)
    }
  }

  override def currentTiePlayer(): Option[AbstractPlayer] = {
    if (tieBreakerIndex < 0 || tieBreakerIndex >= tiedPlayers.size)
      return None
    Some(tiedPlayers(tieBreakerIndex))
  }

  override def requestTieChoice(player: AbstractPlayer): Unit = {
    _waitingForInput = true
    gameLogic.invoke(TieTurnEvent(player))
    gameLogic.invoke(RequestTieChoiceEvent(player, highestAllowedNumber()))
  }

  /**
   * Called when a player has selected a tie-breaker card
   * @param number the index of the selected card
   */
  override def receivedTieBreakerCard(number: Int): Unit = {
    if (!_waitingForInput) throw new IllegalStateException("Not waiting for input")
    _waitingForInput = false

    val player = tiedPlayers(tieBreakerIndex)
    val highestNumber = highestAllowedNumber()
    if (number < 0 || number > highestNumber)
      throw new IllegalArgumentException(s"Selected number $number is out of allowed range (0 to $highestNumber)")

    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    
    val cardManager = gameLogic.cardManager.get
    val card = cardManager.removeCards(number).last
    selectedCard += (player -> card)
    handleNextTieBreakerPlayer()
  }
  
  override def highestAllowedNumber(): Int = {
    if (gameLogic.cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    val remainingCards = gameLogic.cardManager.get.remainingCards
    
    // The highest allowed number is total cards minus the number of tied players already selected
    // This ensures that each tied player can select a unique card
    remainingCards - (tiedPlayers.size - (selectedCard.size + 1)) - 1
  }

  override def isWaitingForInput: Boolean = _waitingForInput
  
  

  override def createSnapshot(): LogicSnapshot[BasePlayerTieLogic.this.type] = BasePlayerTieLogicSnapshot(this).asInstanceOf[LogicSnapshot[BasePlayerTieLogic.this.type]]

  // Getter
  override def getRoundResult: Option[RoundResult] = roundResult
  override def getTiedPlayers: List[AbstractPlayer] = tiedPlayers
  override def getTieBreakerIndex: Int = tieBreakerIndex
  override def getLastNumber: Int = lastNumber
  override def getSelectedCard: Map[AbstractPlayer, Card] = selectedCard

}

class BasePlayerTieLogicSnapshot(
                                  //Round result
                                  val winners: List[AbstractPlayer],
                                  val tricked: List[ResultPlayer],
                                  val notTricked: List[AbstractPlayer],

                                  val tiedPlayers: List[AbstractPlayer],
                                  val tieBreakerIndex: Int,
                                  val lastNumber: Int,
                                  val selectedCard: Map[AbstractPlayer, Card]
                                ) extends LogicSnapshot[BasePlayerTieLogic] {

  def this(logic: BasePlayerTieLogic) = {
    this(
      logic.roundResult.map(_.winners).getOrElse(Nil),
      logic.roundResult.map(_.tricked).getOrElse(Nil),
      logic.roundResult.map(_.notTricked).getOrElse(Nil),
      logic.tiedPlayers,
      logic.tieBreakerIndex,
      logic.lastNumber,
      logic.selectedCard
    )
  }

  override def restore(logic: BasePlayerTieLogic): Unit = {
    if (winners.nonEmpty || tricked.nonEmpty || notTricked.nonEmpty) {
      logic.roundResult = Some(RoundResult(winners, tricked, notTricked))
    } else {
      logic.roundResult = None
    }
    logic.tiedPlayers = tiedPlayers
    logic.tieBreakerIndex = tieBreakerIndex
    logic.lastNumber = lastNumber
    logic.selectedCard = selectedCard
  }
}
