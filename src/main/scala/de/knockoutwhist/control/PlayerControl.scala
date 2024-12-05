package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_INPUT, INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Round, Trick}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

object PlayerControl {

  @tailrec
  def playCard(player: AbstractPlayer, trick: Trick): Card = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_PLAY_CARD, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, true))
    player.handlePlayCard(player.currentHand().get, trick) match {
      case Success(value) =>
        value
      case Failure(exception) =>
        ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
        playCard(player, trick)
    }
  }

  @tailrec
  def dogplayCard(player: AbstractPlayer, round: Round, trick: Trick): Option[Card] = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, RoundLogic.dogNeedsToPlay(round)))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handleDogPlayCard(player.currentHand().get, trick, RoundLogic.dogNeedsToPlay(round)) match {
      case Success(value) =>
        value
      case Failure(exception) =>
        ControlHandler.invoke(ShowErrorStatus(INVALID_INPUT))
        dogplayCard(player, round, trick)
    }
  }

  def determineWinnerTie(players: List[AbstractPlayer]): AbstractPlayer = {
    determineWinnerTie(players, true)
  }

  private def determineWinnerTie(players: List[AbstractPlayer], tieMessage: Boolean): AbstractPlayer = {
    if (!KnockOutWhist.debugmode) CardManager.shuffleAndReset()
    if (tieMessage) ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE))
    var currentStep = 0
    var remaining = CardManager.cardContainer.size - (players.length - 1)
    val cut: mutable.HashMap[AbstractPlayer, Card] = mutable.HashMap()
    for (player <- players) {
      var selCard: Card = null
      while (selCard == null) {
        ControlHandler.invoke(ShowPlayerStatus(SHOW_TIE_NUMBERS, player, remaining))
        player.handlePickTieCard(1, remaining) match {
          case Success(value) =>
            selCard = CardManager.cardContainer(currentStep + (value-1))
            cut.put(player, selCard)
            currentStep += value
            remaining -= (value-1)
          case Failure(exception) =>
            ControlHandler.invoke(ShowErrorStatus(NOT_A_NUMBER))
        }
      }
    }
    ControlHandler.invoke(ShowTieCardsEvent(cut.toList))
    evaluateTieWinner(cut)
  }

  private def evaluateTieWinner(cut: mutable.HashMap[AbstractPlayer, Card]): AbstractPlayer = {
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
      return winner.head
    }
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE_TIE))
    determineWinnerTie(winner.toList, false)
  }

  @tailrec
  def pickNextTrumpsuit(player: AbstractPlayer): Suit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handlePickTrumpsuit() match {
      case Success(value) =>
        value
      case Failure(exception) =>
        ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
        pickNextTrumpsuit(player)
    }
  }
}
