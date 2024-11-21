package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_INPUT, INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_DOG_PLAY_CARD, SHOW_PLAY_CARD, SHOW_TIE_NUMBERS, SHOW_TRUMPSUIT_OPTIONS, SHOW_TURN}
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.DelayHandler
import de.knockoutwhist.utils.events.EventHandler

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}
import scala.util.control.Breaks.*

object PlayerControl {

  def playCard(player: Player): Card = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_PLAY_CARD, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, true))
    ControlHandler.invoke(RequestCardEvent(player.currentHand().get)) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
        playCard(player)
      }
    }
  }

  def dogplayCard(player: Player, round: Round): Option[Card] = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, RoundControl.dogNeedsToPlay(round)))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    ControlHandler.invoke(RequestDogPlayCardEvent(player.currentHand().get, RoundControl.dogNeedsToPlay(round))) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        ControlHandler.invoke(ShowErrorStatus(INVALID_INPUT))
        dogplayCard(player, round)
      }
    }
  }

  def determineWinnerTie(players: List[Player]): Player = {
    determineWinnerTieText(players, true)
  }

  @tailrec
  private def determineWinnerTieText(players: List[Player], tieMessage: Boolean): Player = {
    if (!KnockOutWhist.debugmode) CardManager.shuffleAndReset()
    if (tieMessage) ControlHandler.invoke(ShowGlobalStatus(SHOW_TIE))
    var currentStep = 0
    var remaining = CardManager.cardContainer.size - (players.length - 1)
    val cut: mutable.HashMap[Player, Card] = mutable.HashMap()
    for (player <- players) {
      var selCard: Card = null
      while (selCard == null) {
        ControlHandler.invoke(ShowPlayerStatus(SHOW_TIE_NUMBERS, player, remaining))
        ControlHandler.invoke(RequestNumberEvent(1, remaining)) match {
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

    var currentHighest: Card = null
    val winner: ListBuffer[Player] = ListBuffer()
    for ((player, card) <- cut) {
      if (currentHighest == null) {
        currentHighest = card
        winner += player
      }else {
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
    determineWinnerTieText(winner.toList, false)
  }

  def pickNextTrumpsuit(player: Player): Suit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    ControlHandler.invoke(RequestPickTrumpsuitEvent()) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
        pickNextTrumpsuit(player)
      }
    }
  }
}
