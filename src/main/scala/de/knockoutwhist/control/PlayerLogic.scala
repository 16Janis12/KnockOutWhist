package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.events.ERROR_STATUS.NOT_A_NUMBER
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_TIE_NUMBERS
import de.knockoutwhist.events.cards.ShowTieCardsEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.{SelectTieCommand, TrumpSuitSelectedCommand}

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

object PlayerLogic {
  
  def trumpsuitStep(matchImpl: Match, remaining_players: List[AbstractPlayer]): Suit = {
    if (matchImpl.roundlist.isEmpty) {
      val randomTrumpsuit = CardManager.nextCard().suit
      val newMatchImpl = matchImpl.setNumberOfCards(matchImpl.numberofcards - 1)
      val round = new Round(randomTrumpsuit, remaining_players, true)
      MainLogic.controlRound(newMatchImpl, round)
      randomTrumpsuit
    } else {
      val winner = matchImpl.totalplayers.filter(matchImpl.roundlist.last.winner.name == _.name).head
      val trumpsuit = PlayerControl.pickNextTrumpsuit(winner)
      UndoManager.doStep(TrumpSuitSelectedCommand(matchImpl, trumpsuit, remaining_players, false, winner))
      trumpsuit
    }
  }

  def preSelect(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer]): Unit = {
    if (!KnockOutWhist.debugmode) CardManager.shuffleAndReset()
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE))
    selectTie(winners, matchImpl, round, playersout, immutable.HashMap(), 0, CardManager.cardContainer.size - (winners.length - 1))
  }

  def selectTie(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit = {
    if(currentIndex == winners.size) {
      evaluateTieWinner(matchImpl, round, playersout, cut)
    } else {
      val player = winners(currentIndex)
      ControlHandler.invoke(ShowPlayerStatus(SHOW_TIE_NUMBERS, player, remaining))
      player.handlePickTieCard(1, remaining) match {
        case Success(value) =>
          val selCard = CardManager.cardContainer(currentStep + (value - 1))
          UndoManager.doStep(SelectTieCommand(winners, matchImpl, round, playersout, cut, value, selCard, currentStep, remaining, currentIndex))
        case Failure(exception) =>
          ControlHandler.invoke(ShowErrorStatus(NOT_A_NUMBER))
          selectTie(winners, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex)
      }
    }
  }

  private def evaluateTieWinner(matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card]): Unit = {
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
      MainLogic.endRound(matchImpl, round, winner.head, playersout)
      return
    }
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE_TIE))
    preSelect(winner.toList, matchImpl, round, playersout)
  }

  
}
