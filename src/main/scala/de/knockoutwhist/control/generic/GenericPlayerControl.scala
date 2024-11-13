package de.knockoutwhist.control.generic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_INPUT, INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_DOG_PLAY_CARD, SHOW_PLAY_CARD, SHOW_TIE_NUMBERS, SHOW_TRUMPSUIT_OPTIONS, SHOW_TURN}
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.tui.TUIMain
import de.knockoutwhist.utils.DelayHandler
import de.knockoutwhist.utils.events.EventHandler

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}
import scala.util.control.Breaks.*

object GenericPlayerControl extends EventHandler {

  addListener(TUIMain)
  addListener(DelayHandler)

  override def playCard(player: Player): Card = {
    invoke(ShowPlayerStatus(SHOW_TURN, player))
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    invoke(ShowPlayerStatus(SHOW_PLAY_CARD, player))
    invoke(RenderHandEvent(player.currentHand().get, true))
    invoke(RequestCardEvent(player.currentHand().get)) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        invoke(ShowErrorStatus(INVALID_NUMBER))
        playCard(player)
      }
    }
  }

  override def dogplayCard(player: Player, round: Round): Option[Card] = {
    invoke(ShowPlayerStatus(SHOW_TURN, player))
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    invoke(ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, round.dogNeedsToPlay))
    invoke(RenderHandEvent(player.currentHand().get, false))
    invoke(RequestDogPlayCardEvent(player.currentHand().get, round.dogNeedsToPlay)) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        invoke(ShowErrorStatus(INVALID_INPUT))
        dogplayCard(player, round)
      }
    }
  }

  override def determineWinnerTie(players: List[Player]): Player = {
    determineWinnerTieText(players, true)
  }

  @tailrec
  private def determineWinnerTieText(players: List[Player], tieMessage: Boolean): Player = {
    if (!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    if (tieMessage) invoke(ShowGlobalStatus(SHOW_TIE))
    var currentStep = 0
    var remaining = CardManager.cardContainer.size - (players.length - 1)
    val cut: mutable.HashMap[Player, Card] = mutable.HashMap()
    for (player <- players) {
      var selCard: Card = null
      while (selCard == null) {
        invoke(ShowPlayerStatus(SHOW_TIE_NUMBERS, player, remaining))
        invoke(RequestNumberEvent(1, remaining)) match {
          case Success(value) =>
            selCard = CardManager.cardContainer(currentStep + (value-1))
            cut.put(player, selCard)
            currentStep += value
            remaining -= (value-1)
          case Failure(exception) =>
            invoke(ShowErrorStatus(NOT_A_NUMBER))
        }
      }
    }
    invoke(ShowTieCardsEvent(cut.toList))

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
      invoke(ShowGlobalStatus(SHOW_TIE_WINNER, winner.head))
      return winner.head
    }
    invoke(ShowGlobalStatus(SHOW_TIE_TIE))
    determineWinnerTieText(winner.toList, false)
  }

  override def pickNextTrumpsuit(player: Player): Suit = {
    invoke(ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player))
    invoke(RenderHandEvent(player.currentHand().get, false))
    invoke(RequestPickTrumpsuitEvent()) match {
      case Success(value) => {
        value
      }
      case Failure(exception) => {
        invoke(ShowErrorStatus(INVALID_NUMBER))
        pickNextTrumpsuit(player)
      }
    }
  }
}
