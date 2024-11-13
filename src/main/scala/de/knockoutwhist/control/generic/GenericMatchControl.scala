package de.knockoutwhist.control.generic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.generic.GenericPlayerControl.addListener
import de.knockoutwhist.control.{MatchControl, PlayerControl}
import de.knockoutwhist.events.ERROR_STATUS.{IDENTICAL_NAMES, INVALID_NAME_FORMAT, INVALID_NUMBER, INVALID_NUMBER_OF_PLAYERS}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_EXIT_GAME, SHOW_GAME_RUNNING, SHOW_MENU, SHOW_START_MATCH, SHOW_TYPE_PLAYERS, SHOW_WELCOME}
import de.knockoutwhist.events.MATCH_STATUS.SHOW_FINISHED
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.{GLOBAL_STATUS, ShowErrorStatus, ShowGlobalStatus, ShowMatchStatus, ShowPlayerStatus, ShowRoundStatus, ShowStatusEvent}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.tui.TUIMain
import de.knockoutwhist.utils.{CustomPlayerQueue, DelayHandler}
import de.knockoutwhist.utils.events.EventHandler

import scala.compiletime.uninitialized
import scala.io.StdIn
import scala.util.Random

object GenericMatchControl extends EventHandler {

  addListener(TUIMain)
  addListener(DelayHandler)

  private[control] var playerQueue: CustomPlayerQueue[Player] = uninitialized
  private var init = false

  override def initial(): Boolean = {
    if (init) {
      invoke(ShowGlobalStatus(SHOW_GAME_RUNNING))
      return false
    }
    init = true
    invoke(ShowGlobalStatus(SHOW_WELCOME))
    start()
    true
  }

  override def start(): Unit = {
    while (true) { //Main Gameplay Loop
      val input = printMenu()
      input match {
        case "1" =>
          startMatch()
        case "2" =>
          invoke(ShowGlobalStatus(SHOW_EXIT_GAME))
          return
        case _ =>
          invoke(ShowErrorStatus(INVALID_NUMBER))
      }
    }
  }

  private[control] def startMatch(): Player = {
    clearConsole()
    invoke(ShowGlobalStatus(SHOW_START_MATCH))
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[Player](players, Random.nextInt(players.length))
    clearConsole()
    controlMatch()
  }

  private[control] def enterPlayers(): Array[Player] = {
    invoke(ShowGlobalStatus(SHOW_TYPE_PLAYERS))
    val names = StdIn.readLine().split(",")
    if (names.length < 2) {
      invoke(ShowErrorStatus(INVALID_NUMBER_OF_PLAYERS))
      return enterPlayers()
    }
    if (names.distinct.length != names.length) {
      invoke(ShowErrorStatus(IDENTICAL_NAMES))
      return enterPlayers()
    }
    if (names.count(_.trim.isBlank) > 0 || names.count(_.trim.length <= 2) > 0 || names.count(_.trim.length > 10) > 0) {
      invoke(ShowErrorStatus(INVALID_NAME_FORMAT))
      return enterPlayers()
    }
    names.map(s => Player(s))
  }

  private[control] def controlMatch(): Player = {
    val matchImpl = Match(playerQueue.toList)
    while (!matchImpl.isOver) {
      val roundImpl = controlRound(matchImpl)
    }
    clearConsole()
    val playerwinner = matchImpl.finalizeMatch().name
    invoke(ShowMatchStatus(SHOW_FINISHED, matchImpl, playerwinner))
    matchImpl.finalizeMatch()
  }

  private[control] def controlRound(matchImpl: Match): Round = {
    val roundImpl = nextRound(matchImpl)
    clearConsole(10)
    invoke(ShowRoundStatus(SHOW_START_ROUND, roundImpl))
    clearConsole(2)
    while (!roundImpl.isOver) {
      controlTrick(roundImpl)
    }
    val (roundWinner, finalRound) = roundImpl.finalizeRound()
    invoke(ShowRoundStatus(WON_ROUND, finalRound, roundWinner))
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(5000L)
    if (finalRound.players_out.nonEmpty) {
      invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
      finalRound.players_out.foreach(p => {
        playerQueue.remove(p)
      })
    }
    playerQueue.resetAndSetStart(roundWinner)
    finalRound
  }

  private[control] def controlTrick(round: Round): Trick = {
    val trick = nextTrick(round)
    for (player <- playerQueue) {
      clearConsole()
      println(printTrick(round))
      if (!player.doglife) {
        val rightCard = controlSuitplayed(trick, player)
        player.removeCard(rightCard)
        trick.playCard(rightCard, player)
      } else if (player.currentHand().exists(_.cards.nonEmpty)) {
        val card = GenericPlayerControl.dogplayCard(player, round)
        if (card.isEmpty) {
          invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
        } else {
          player.removeCard(card.get)
          trick.playCard(card.get, player)
        }
      }
    }
    val (winner, finalTrick) = trick.wonTrick()
    clearConsole()
    println(printTrick(round))
    invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, winner))
    clearConsole(2)
    playerQueue.resetAndSetStart(winner)
    if (!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    finalTrick
  }

  private[control] def controlSuitplayed(trick: Trick, player: Player): Card = {
    var card = GenericPlayerControl.playCard(player)
    if (trick.get_first_card().isDefined) {
      while (!(trick.get_first_card().get.suit == card.suit)) {
        var hasSuit = false
        for (cardInHand <- player.currentHand().get.cards) {
          if (cardInHand.suit == trick.get_first_card().get.suit) {
            hasSuit = true
          }
        }
        if (!hasSuit) {
          return card
        } else {
          println(f"You have to play a card of suit: ${trick.get_first_card().get.suit}\n")
          card = GenericPlayerControl.playCard(player)
        }
      }
    }
    card
  }

  private[control] def printMenu(): String = {
    invoke(ShowGlobalStatus(SHOW_MENU))
    StdIn.readLine()
  }

  private[control] def printTrick(round: Round): String = {
    val sb = new StringBuilder()
    sb.append("Current Trick:\n")
    sb.append("Trump-Suit: " + round.trumpSuit + "\n")
    if (round.get_current_trick().get_first_card().isDefined) {
      sb.append(s"Suit to play: ${round.get_current_trick().get_first_card().get.suit}\n")
    }
    for ((card, player) <- round.get_current_trick().cards) {
      sb.append(s"${player.name} played ${card.toString}\n")
    }
    sb.toString()
  }

  private def clearConsole(lines: Int = 32): Int = {
    var l = 0
    for (_ <- 0 until lines) {
      println()
      l += 1
    }
    l
  }

  override def nextRound(matchImpl: Match): Round = {
    if (matchImpl.isOver) {
      println(s"The match is over. The winner is ${matchImpl.finalizeMatch().name}.")
      return null
    }
    matchImpl.create_round()
  }

  override def nextTrick(roundImpl: Round): Trick = {
    if (roundImpl.isOver) {
      println("The round is over.")
      return null
    }
    roundImpl.create_trick()
  }
}
