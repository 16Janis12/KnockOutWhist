package de.knockoutwhist.control.generic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_TIE_NUMBERS
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.RequestNumberEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.tui.TUIMain
import de.knockoutwhist.utils.events.EventHandler

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import scala.util.{Failure, Success}
import scala.util.control.Breaks.*

object GenericPlayerControl extends EventHandler {

  addListener(TUIMain)

  override def playCard(player: Player): Card = {
    println("It's your turn, " + player.name + ".")
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    println("Which card do you want to play?")
    invoke(RenderHandEvent(player.currentHand().get, true))
    try {
      val card = readLine().toInt - 1
      val handCard = player.currentHand()
      if (handCard.isEmpty) {
        println("You don't have any cards.")
        throw new IllegalStateException("Trying to play a card without any cards.")
      } else if (card < 0 || card >= handCard.get.cards.length) {
        println("Please enter a valid number.")
        playCard(player)
      } else {
        handCard.get.cards(card)
      }
    } catch {
      case e: NumberFormatException =>
        println("Please enter a valid number.")
        playCard(player)
    }

  }

  override def dogplayCard(player: Player, round: Round): Option[Card] = {
    println("It's your turn, " + player.name + ".")
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    println("You are using your dog life. Do you want to play your final card now?")
    if (round.dogNeedsToPlay) {
      println("You have to play your final card this round!")
      println("Please enter y to play your final card.")
    } else {
      println("Please enter y/n to play your final card.")
    }

    invoke(RenderHandEvent(player.currentHand().get, false))
    val card = readLine()
    val handCard = player.currentHand()
    if (handCard.isEmpty) {
      println("You don't have any cards.")
      throw new IllegalStateException("Trying to play a card without any cards.")
    } else if (card.equalsIgnoreCase("y")) {
      Some(handCard.get.cards.head)
    } else if (card.equalsIgnoreCase("n") && !round.dogNeedsToPlay) {
      None
    } else {
      println("Please enter y or n to play your final card.")
      dogplayCard(player, round)
    }
  }

  override def determineWinnerTie(players: List[Player]): Player = {
    determineWinnerTieText(players, true)
  }

  @tailrec
  private def determineWinnerTieText(players: List[Player], tieMessage: Boolean): Player = {
    if (!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    if (tieMessage) println("It's a tie! Let's cut to determine the winner.")
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
    println("Which suit do you want to pick as the next trump suit?")
    println("1: Hearts")
    println("2: Diamonds")
    println("3: Clubs")
    println("4: Spades")
    println()

    invoke(RenderHandEvent(player.currentHand().get, false))

    try {
      val suit = readLine().toInt
      suit match {
        case 1 => Suit.Hearts
        case 2 => Suit.Diamonds
        case 3 => Suit.Clubs
        case 4 => Suit.Spades
        case _ =>
          println("Please enter a valid number.")
          pickNextTrumpsuit(player)
      }
    } catch {
      case e: NumberFormatException =>
        println("Please enter a valid number.")
        pickNextTrumpsuit(player)
    }
  }

  override def showWon(player: Player, round: Round): String = {
    s"$player won this round."
  }

}
