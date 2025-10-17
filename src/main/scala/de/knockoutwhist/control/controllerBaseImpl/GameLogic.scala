package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{MatchUtil, RoundResult, RoundUtil, TrickUtil}
import de.knockoutwhist.control.controllerBaseImpl.sublogic.{PlayerInputLogic, PlayerTieLogic, UndoManager}
import de.knockoutwhist.events.GLOBAL_STATUS.SHOW_FINISHED_MATCH
import de.knockoutwhist.events.ShowGlobalStatus
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.events.EventHandler
import org.apache.pekko.io.dns.IdGenerator.Policy
import org.apache.pekko.io.dns.IdGenerator.Policy.ThreadLocalRandom

import java.util.UUID

/*
  Main game logic controller
*/

final class GameLogic (val config: Configuration) extends EventHandler {
  //Constants
  val ID: UUID = UUID.randomUUID()

  //Logics
  val playerTieLogic: PlayerTieLogic = PlayerTieLogic(this)
  val playerInputLogic: PlayerInputLogic = PlayerInputLogic(this)
  val undoManager: UndoManager = UndoManager(this)

  //Variables
  private[control] var cardManager: Option[CardManager] = Some(config.cardManager)
  
  private[control] var currentMatch: Option[Match] = None
  private[control] var currentRound: Option[Round] = None
  private[control] var currentTrick: Option[Trick] = None
  private[control] var currentPlayer: Option[AbstractPlayer] = None
  private[control] var playerQueue: Option[CustomPlayerQueue[AbstractPlayer]] = None

  def createSession(): Unit = {
    cardManager = Some(config.cardManager)

    currentMatch = None
    currentRound = None
    currentTrick = None
    currentPlayer = None
    playerQueue = None
  }

  def createMatch(players: List[AbstractPlayer]): Match = {
    val matchImpl = Match(totalplayers = players)
    currentMatch = Some(matchImpl)
    matchImpl
  }

  private[control] def controlMatch(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get

    //TODO update persistence

    if (matchImpl.isOver) {

      //TODO inform observers

    } else {
      if (matchImpl.roundlist.isEmpty) {
        if (cardManager.isEmpty) throw new IllegalStateException("No card manager set")
        val cardManagerImpl = cardManager.get
        val firstCard = cardManagerImpl.nextCard()
        val newRound = RoundUtil.createRound(firstCard.suit)

        providePlayersWithCards()
        
        val randomPlayer: Int = .nextInt(matchImpl.playersIn.size)
        playerQueue = Some(config.createRightQueue(matchImpl.playersIn.toArray, matchImpl.startingplayerindex))
        //TODO inform observers about first round and new cards

        currentRound = Some(newRound)
        controlRound()
        return
      }
      currentMatch = Some(matchImpl.setNumberOfCards(matchImpl.numberofcards - 1))
      providePlayersWithCards()
      //TODO inform observers about new round and new cards

      //Check if the last round had a winner
      val lastRound = matchImpl.roundlist.last
      if (lastRound.winner.isEmpty)
        throw new IllegalStateException("Last round had no winner")
      val lastWinner = lastRound.winner.get

      //TODO inform observers about that a player needs to pick trump suit

      playerInputLogic.requestTrumpsuit(lastWinner)
    }
  }
  
  private[control] def controlRound(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get
    if (currentRound.isEmpty) throw new IllegalStateException("No current round set")
    val roundImpl = currentRound.get

    //TODO update persistence

    if (MatchUtil.isRoundOver(matchImpl, roundImpl)) {
      val roundResult: RoundResult = RoundUtil.finishRound(roundImpl, matchImpl)
      if (roundResult.isTie) {

        //TODO inform observers about tie
        //TODO delay for a moment to show the tie

        playerTieLogic.handleTie(roundResult)
        return
      }
      val newMatch = endRound(roundResult.winners.head, roundResult)
      currentMatch = Some(newMatch)
      controlMatch()
    } else {

      //TODO Inform observers about new trick

      val trick = Trick()
      currentTrick = Some(trick)
      controlTrick()
    }
  }

  private def endRound(winner: AbstractPlayer, roundResult: RoundResult): Match = {
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

    //TODO show who won the round
    //TODO delay for a moment to show the round winner

    if (roundResult.notTricked.nonEmpty) {
      if (matchImpl.dogLife) {
        // TODO show who is out
        // TODO delay for a moment to show who is out
        matchImpl = matchImpl.updatePlayersIn(matchImpl.playersIn.filterNot(roundResult.notTricked.contains(_)))
      } else {
        //TODO show who became dog
        //TODO delay for a moment to show who became dog
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
  
  private[control] def controlTrick(): Unit = {
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
      //TODO show who won the trick
      queueImpl.resetAndSetStart(winner)
      //TODO Delay for a moment to show the trick winner
      controlRound()
    } else {
      controlPlayerPlay()
    }
  }

  private def endTrick(): Round = {
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
  
  private[control] def controlPlayerPlay(): Unit = {
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
    if (playerImpl.isInDoglife) {

      //TODO inform observers about player's turn to play in dog_life

      playerInputLogic.requestDog(playerImpl)
    } else {

      //TODO inform observers about player's turn to play

      playerInputLogic.requestCard(playerImpl)
    }
  }


  //
  private[control] def providePlayersWithCards(): Unit = {
    if (currentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = currentMatch.get
    if (cardManager.isEmpty) throw new IllegalStateException("No card manager set")
    val cardManagerImpl = cardManager.get

    cardManagerImpl.shuffleAndReset()

    val handSize = matchImpl.numberofcards

    matchImpl.playersIn.foreach(player => {
      val hand = if (player.isInDoglife) {
        cardManagerImpl.createHand(handSize)
      } else {
        cardManagerImpl.createHand(1)
      }
      player.provideHand(hand)
    })
  }

}
