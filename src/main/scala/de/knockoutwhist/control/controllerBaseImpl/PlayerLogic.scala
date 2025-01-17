package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.control.{ControlHandler, Playerlogcomponent}
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_TIE_NUMBERS
import de.knockoutwhist.events.cards.ShowTieCardsEvent
import de.knockoutwhist.events.ui.GameState.{INGAME, TIE}
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.ui.gui.TieMenu
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.{SelectTieCommand, TrumpSuitSelectedCommand}

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

object PlayerLogic extends Playerlogcomponent {
  
  def trumpsuitStep(matchImpl: Match, remaining_players: List[AbstractPlayer]): Unit = {
    if (matchImpl.roundlist.isEmpty) {
      val randomTrumpsuit = matchImpl.cardManager.nextCard().suit
      val newMatchImpl = matchImpl.setNumberOfCards(matchImpl.numberofcards - 1)
      val round = new Round(randomTrumpsuit, remaining_players, true)
      KnockOutWhist.config.maincomponent.controlRound(newMatchImpl, round)
    } else {
      val winner = matchImpl.totalplayers.filter(matchImpl.roundlist.last.winner.name == _.name).head
      KnockOutWhist.config.playeractrcomponent.pickNextTrumpsuit(matchImpl, remaining_players, false, winner)
    }
  }

  def trumpSuitSelected(matchImpl: Match, suit: Try[Suit], remaining_players: List[AbstractPlayer], firstRound: Boolean, decided: AbstractPlayer): Unit = {
    if (suit.isFailure) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
      KnockOutWhist.config.playeractrcomponent.pickNextTrumpsuit(matchImpl, remaining_players, firstRound, decided)
      return
    }
    ControlHandler.invoke(GameStateUpdateEvent(INGAME))
    UndoManager.doStep(TrumpSuitSelectedCommand(matchImpl, suit.get, remaining_players, false, decided))
  }

  def preSelect(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer]): Unit = {
    if (!KnockOutWhist.debugmode) matchImpl.cardManager.shuffleAndReset()
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE))
    ControlHandler.invoke(GameStateUpdateEvent(TIE))
    selectTie(winners, matchImpl, round, playersout, immutable.HashMap(), 0, matchImpl.cardManager.cardContainer.size - (winners.length - 1))
  }

  def selectTie(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    if(currentIndex == winners.size) {
      evaluateTieWinner(matchImpl, round, playersout, cut)
    } else {
      val player = winners(currentIndex)
      ControlHandler.invoke(ShowPlayerStatus(SHOW_TIE_NUMBERS, player, remaining))
      
      player.handlePickTieCard(winners, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex)
    }
  }

  def selectedTie(winner: List[AbstractPlayer],matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], value: Try[Int], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    if (value.isFailure) {
      ControlHandler.invoke(ShowErrorStatus(NOT_A_NUMBER))
      selectTie(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex)
      return
    }
    val selCard = matchImpl.cardManager.cardContainer(currentStep + (value.get - 1))
    UndoManager.doStep(SelectTieCommand(winner, matchImpl, round, playersout, cut, value.get, selCard, currentStep, remaining, currentIndex))
  }

  def evaluateTieWinner(matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card]): Unit = {
    ControlHandler.invoke(ShowTieCardsEvent(cut.toList))
    val winner: ListBuffer[AbstractPlayer] = ListBuffer()
    var currentHighest: Card = null
    for ((player, card) <- cut) {
      if (currentHighest == null) {
        currentHighest = card
        winner += player
      } else {
        val compared = card.cardValue.ordinal.compareTo(currentHighest.cardValue.ordinal)
        if (compared > 0) {
          currentHighest = card
          winner.clear()
          winner += player
        } else if (compared == 0) {
          winner += player
        }
      }
    }
    if (winner.size == 1) {
      ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE_WINNER, winner.head))
      KnockOutWhist.config.maincomponent.endRound(matchImpl, round, winner.head, playersout)
      return
    }
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE_TIE))
    preSelect(winner.toList, matchImpl, round, playersout)
  }

  
}
