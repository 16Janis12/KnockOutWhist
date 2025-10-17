package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager}
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.{GameLogic, GameState, LogicSnapshot}
import de.knockoutwhist.control.GameState.{FinishedMatch, InGame, Lobby, SelectTrump, TieBreak}
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{MatchUtil, RoundResult, RoundUtil, TrickUtil}
import de.knockoutwhist.control.controllerBaseImpl.sublogic.{BasePlayerInputLogic, BasePlayerTieLogic, BaseUndoManager}
import de.knockoutwhist.events.global.tie.TieEvent
import de.knockoutwhist.events.global.{GameStateChangeEvent, MatchEndEvent, NewRoundEvent, NewTrickEvent, RoundEndEvent, ShowDogsEvent, ShowPlayersOutEvent, TrickEndEvent, TrumpSelectEvent, TurnEvent}
import de.knockoutwhist.events.old.GLOBAL_STATUS.SHOW_FINISHED_MATCH
import de.knockoutwhist.events.old.ShowGlobalStatus
import de.knockoutwhist.events.player.ReceivedHandEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.{AbstractPlayer, PlayerData}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.events.EventHandler

import java.util.UUID
import scala.util.Random

/*
  Main game logic controller
*/
final class BaseGameLogic(val config: Configuration) extends EventHandler with GameLogic {
  //Constants
  val ID: UUID = UUID.randomUUID()

  //Logics
  val playerTieLogic: BasePlayerTieLogic = BasePlayerTieLogic(this)
  val playerInputLogic: BasePlayerInputLogic = BasePlayerInputLogic(this)
  val undoManager: BaseUndoManager = BaseUndoManager(this)
  
  //Game State
  private[control] var state: GameState = GameState.MainMenu

  //Variables
  private[control] var cardManager: Option[CardManager] = Some(config.cardManager)
  
  private[control] var currentMatch: Option[Match] = None
  private[control] var currentRound: Option[Round] = None
  private[control] var currentTrick: Option[Trick] = None
  private[control] var currentPlayer: Option[AbstractPlayer] = None
  private[control] var playerQueue: Option[CustomPlayerQueue[AbstractPlayer]] = None

  override def createSession(): Unit = {
    cardManager = Some(config.cardManager)

    currentMatch = None
    currentRound = None
    currentTrick = None
    currentPlayer = None
    playerQueue = None
    state = Lobby
  }

  override def createMatch(players: List[AbstractPlayer]): Match = {
    val matchImpl = Match(totalplayers = players)
    currentMatch = Some(matchImpl)
    matchImpl
  }

  override def controlMatch(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get

    //TODO update persistence

    if (matchImpl.isOver) {
      //Winner is the last person in the playersIn list
      val winner = matchImpl.playersIn.head
      
      invoke(GameStateChangeEvent(state, FinishedMatch))
      state = FinishedMatch
      invoke(MatchEndEvent(winner))
    } else {
      if (matchImpl.roundlist.isEmpty) {
        if (cardManager.isEmpty) throw new IllegalStateException("No card manager set")
        val cardManagerImpl = cardManager.get
        val firstCard = cardManagerImpl.nextCard()
        val newRound = RoundUtil.createRound(firstCard.suit)

        providePlayersWithCards()
        
        val randomPlayer: Int = Random.nextInt(matchImpl.playersIn.size)
        playerQueue = Some(config.createRightQueue(matchImpl.playersIn.toArray, randomPlayer))
        
        matchImpl.playersIn.foreach(player => {invoke(ReceivedHandEvent(player))})

        currentRound = Some(newRound)

        invoke(NewRoundEvent())
        invoke(DelayEvent(500))
        
        controlRound()
        return
      }
      currentMatch = Some(matchImpl.setNumberOfCards(matchImpl.numberofcards - 1))
      providePlayersWithCards()

      matchImpl.playersIn.foreach(player => {invoke(ReceivedHandEvent(player))})

      //Check if the last round had a winner
      val lastRound = matchImpl.roundlist.last
      if (lastRound.winner.isEmpty)
        throw new IllegalStateException("Last round had no winner")
      val lastWinner = lastRound.winner.get

      //Create new player queue starting with last round winner
      
      playerQueue = Some(config.createRightQueue(
        matchImpl.playersIn.toArray,
        matchImpl.playersIn.indexOf(lastWinner)
      ))

      invoke(GameStateChangeEvent(state, SelectTrump))
      state = SelectTrump

      invoke(TrumpSelectEvent(lastWinner))
      
      playerInputLogic.requestTrumpSuit(lastWinner)
    }
  }
  
  override def controlRound(): Unit = {
    if (state != InGame)
      invoke(GameStateChangeEvent(state, InGame))
    state = InGame
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get
    if (currentRound.isEmpty) throw new IllegalStateException("No current round set")
    val roundImpl = currentRound.get

    //TODO update persistence

    if (MatchUtil.isRoundOver(matchImpl, roundImpl)) {
      val roundResult: RoundResult = RoundUtil.finishRound(roundImpl, matchImpl)
      if (roundResult.isTie) {
        state = TieBreak

        invoke(TieEvent(roundResult.winners))
        invoke(DelayEvent(2000))

        playerTieLogic.handleTie(roundResult)
        return
      }
      val newMatch = endRound(roundResult.winners.head, roundResult)
      currentMatch = Some(newMatch)
      controlMatch()
    } else {
      
      invoke(NewTrickEvent())
      invoke(DelayEvent(1000))

      val trick = Trick()
      currentTrick = Some(trick)
      controlTrick()
    }
  }

  override def endRound(winner: AbstractPlayer, roundResult: RoundResult): Match = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    var matchImpl = currentMatch.get
    if (currentRound.isEmpty) throw new IllegalStateException("No current round set")
    val roundImpl = currentRound.get
    //Create final round snapshot
    val resultingRound = Round(
      trumpSuit = roundImpl.trumpSuit,
      firstRound = roundImpl.firstRound,
      tricklist = roundImpl.tricklist,
      winner = Some(winner)
    )
    
    invoke(RoundEndEvent(winner))
    invoke(DelayEvent(2000))

    if (roundResult.notTricked.nonEmpty) {
      if (matchImpl.dogLife) {
        invoke(ShowPlayersOutEvent(roundResult.notTricked))
        invoke(DelayEvent(2000))
        matchImpl = matchImpl.updatePlayersIn(matchImpl.playersIn.filterNot(roundResult.notTricked.contains(_)))
      } else {
        invoke(ShowDogsEvent(roundResult.notTricked))
        invoke(DelayEvent(2000))
        matchImpl = matchImpl.setDogLife()
        // Make players dogs
        roundResult.notTricked.foreach(player => {
          player.setDogLife()
        })
      }
    }
    roundResult.tricked.foreach(player => {
      player.resetDogLife()
    })
    matchImpl.addRound(resultingRound)
  }
  
  override def controlTrick(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get
    if (playerQueue.isEmpty) throw new IllegalStateException("No player queue set")
    val queueImpl = playerQueue.get
    if (currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = currentTrick.get

    //TODO update persistence

    if (TrickUtil.isOver(matchImpl, queueImpl)) {
      val newRound = endTrick()
      if (newRound.tricklist.isEmpty || newRound.tricklist.last.winner.isEmpty) throw new IllegalStateException("Trick has no winner after ending trick")
      val winner = newRound.tricklist.last.winner.get
      currentRound = Some(newRound)
      
      invoke(TrickEndEvent(winner))
      invoke(DelayEvent(2000))
      
      queueImpl.resetAndSetStart(winner)
      controlRound()
    } else {
      controlPlayerPlay()
    }
  }

  override def endTrick(): Round = {
    if (currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = currentTrick.get
    if (currentRound.isEmpty) throw new IllegalStateException("No current round set")
    val roundImpl = currentRound.get
    val resultTrick = TrickUtil.finishTrick(trickImpl, roundImpl)
    val resultingTrick = Trick(
      cards = trickImpl.cards,
      winner = Some(resultTrick.winner),
      firstCard = trickImpl.firstCard
    )
    roundImpl.addTrick(resultingTrick)
  }
  
  override def controlPlayerPlay(): Unit = {
    if (playerQueue.isEmpty) throw new IllegalStateException("No player queue set")
    val queueImpl = playerQueue.get
    val playerImpl = queueImpl.nextPlayer()
    currentPlayer = Some(playerImpl)
    if (playerImpl.currentHand().isEmpty) {
      controlTrick()
      return
    }
    val handImpl = playerImpl.currentHand().get
    if (handImpl.cards.isEmpty) {
      controlTrick()
      return
    }
    invoke(TurnEvent(playerImpl))
    playerInputLogic.requestCard(playerImpl)
  }


  //
  override def providePlayersWithCards(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get
    if (cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    val cardManagerImpl = cardManager.get

    cardManagerImpl.shuffleAndReset()

    val handSize = matchImpl.numberofcards

    matchImpl.playersIn.foreach(player => {
      val hand = if (player.isInDogLife) {
        cardManagerImpl.createHand(handSize)
      } else {
        cardManagerImpl.createHand(1)
      }
      player.provideHand(hand)
    })
  }
  
  
  
  // Getters
  
  def getCurrentState: GameState = state
  
  def getCurrentMatch: Option[Match] = currentMatch
  
  def getCurrentRound: Option[Round] = currentRound
  
  def getCurrentTrick: Option[Trick] = currentTrick
  
  def getCurrentPlayer: Option[AbstractPlayer] = currentPlayer

  def getPlayerQueue: Option[CustomPlayerQueue[AbstractPlayer]] = playerQueue
  
  
  //Snapshotting
  override def createSnapshot(): LogicSnapshot[BaseGameLogic] = BaseGameLogicSnapshot(this)
}

class BaseGameLogicSnapshot(
                           val savedState: GameState,
                           //Card Manager
                           val cardContainer: Option[List[Card]],
                           val cardIndex: Option[Int],

                           val currentMatch: Option[Match],
                           val currentRound: Option[Round],
                           val currentTrick: Option[Trick],
                           val currentPlayer: Option[AbstractPlayer],
                           
                           //Custom Player Queue
                           val playerIndex: Option[Int],
                           val players: Option[List[AbstractPlayer]],

                           val playerStates: Map[UUID, PlayerData]
                           
                           ) extends LogicSnapshot[BaseGameLogic] {
  
  def this(gameLogic: BaseGameLogic) = {
    this(
      gameLogic.state,
      
      gameLogic.cardManager.map(cm => cm.cardContainer),
      gameLogic.cardManager.map(cm => cm.currentIndx),
      
      gameLogic.currentMatch,
      gameLogic.currentRound,
      gameLogic.currentTrick,
      gameLogic.currentPlayer,
      
      gameLogic.playerQueue.map(pq => pq.currentIndex),
      gameLogic.playerQueue.map(pq => pq.toList),
      
      gameLogic.currentMatch match {
        case Some(m) => m.totalplayers.map(p => (p.id, p.generatePlayerData())).toMap
        case None => Map.empty[UUID, PlayerData]
      }
    )
  }
  

  override def restore(logic: BaseGameLogic): Unit = {
    logic.state = savedState

    //Card Manager
    if (logic.cardManager.isDefined) {
      val cardManagerImpl = logic.cardManager.get
      if (cardContainer.isDefined && cardIndex.isDefined)
        cardManagerImpl.setState(cardContainer.get, cardIndex.get)
    }
    

    logic.currentMatch = currentMatch
    logic.currentRound = currentRound
    logic.currentTrick = currentTrick
    logic.currentPlayer = currentPlayer

    //Custom Player Queue
    if (logic.playerQueue.isDefined) {
      if (players.isDefined || playerIndex.isDefined)
        logic.playerQueue = Some(logic.config.createRightQueue(
          players.get.toArray,
          playerIndex.get
        ))
    }

    //Player States
    logic.currentMatch match {
      case Some(m) =>
        m.totalplayers.foreach(player => {
          val dataOpt = playerStates.get(player.id)
          if (dataOpt.isDefined)
            player.receivePlayerData(dataOpt.get)
        })
      case None => //Do nothing
    }
  }
}
