package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager}
import de.knockoutwhist.control.{MatchControl, PlayerControl}
import de.knockoutwhist.events.ERROR_STATUS.{IDENTICAL_NAMES, INVALID_NAME_FORMAT, INVALID_NUMBER, INVALID_NUMBER_OF_PLAYERS}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_EXIT_GAME, SHOW_GAME_RUNNING, SHOW_MENU, SHOW_START_MATCH, SHOW_TYPE_PLAYERS, SHOW_WELCOME}
import de.knockoutwhist.events.MATCH_STATUS.SHOW_FINISHED
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.directional.RequestNumberEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{GLOBAL_STATUS, ShowErrorStatus, ShowGlobalStatus, ShowMatchStatus, ShowPlayerStatus, ShowRoundStatus, ShowStatusEvent}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.{CustomPlayerQueue, DelayHandler}
import de.knockoutwhist.utils.events.EventHandler
import de.knockoutwhist.utils.Implicits.*

import scala.compiletime.uninitialized
import scala.io.StdIn
import scala.util.{Failure, Random, Success, Try}

object MatchControl {

  private[control] var playerQueue: CustomPlayerQueue[Player] = uninitialized
  private var init = false

  def initial(): Boolean = {
    if (init) {
      ControlHandler.invoke(ShowGlobalStatus(SHOW_GAME_RUNNING))
      return false
    }
    init = true
    ControlHandler.invoke(ShowGlobalStatus(SHOW_WELCOME))
    start()
    true
  }

  def start(): Unit = {
    while (true) { //Main Gameplay Loop
      val input = printMenu()
      input match {
        case Success(value) => {
          if (value == 1) {
            startMatch()
          } else if (value == 2) {
            ControlHandler.invoke(ShowGlobalStatus(SHOW_EXIT_GAME))
            return
          } else {
            ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
          }
        }
        case Failure(exception) => {
          ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
        }
      }
    }
  }

  def startMatch(): Player = {
    clearConsole()
    ControlHandler.invoke(ShowGlobalStatus(SHOW_START_MATCH))
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[Player](players, Random.nextInt(players.length))
    clearConsole()
    controlMatch()
  }

  def enterPlayers(): Array[Player] = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TYPE_PLAYERS))
    val names = StdIn.readLine().split(",")
    if (names.length < 2) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER_OF_PLAYERS))
      return enterPlayers()
    }
    if (names.distinct.length != names.length) {
      ControlHandler.invoke(ShowErrorStatus(IDENTICAL_NAMES))
      return enterPlayers()
    }
    if (names.count(_.trim.isBlank) > 0 || names.count(_.trim.length <= 2) > 0 || names.count(_.trim.length > 10) > 0) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NAME_FORMAT))
      return enterPlayers()
    }
    names.map(s => Player(s))
  }

  def controlMatch(): Player = {
    val matchImpl = Match(playerQueue.toList)
    while (!isOver(matchImpl)) {
      val roundImpl = controlRound(matchImpl)
    }
    clearConsole()
    val winner = finalizeMatch(matchImpl)
    val playerwinner = winner.name
    ControlHandler.invoke(ShowMatchStatus(SHOW_FINISHED, matchImpl, playerwinner))
    winner
  }

  def controlRound(matchImpl: Match): Round = {
    val roundImpl = nextRound(matchImpl)
    clearConsole(10)
    ControlHandler.invoke(ShowRoundStatus(SHOW_START_ROUND, roundImpl))
    clearConsole(2)
    while (!RoundControl.isOver(roundImpl)) {
      controlTrick(roundImpl)
    }
    val (roundWinner, finalRound) = RoundControl.finalizeRound(roundImpl, matchImpl)
    ControlHandler.invoke(ShowRoundStatus(WON_ROUND, finalRound, roundWinner))
    if (!KnockOutWhist.DEBUG_MODE) ControlHandler.invoke(DelayEvent(5000L))
    if (finalRound.players_out.nonEmpty) {
      ControlHandler.invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
      finalRound.players_out.foreach(p => {
        playerQueue.remove(p)
      })
    }
    playerQueue.resetAndSetStart(roundWinner)
    finalRound
  }

  def controlTrick(round: Round): Trick = {
    val trick = nextTrick(round)
    for (player <- playerQueue) {
      clearConsole()
      println(printTrick(round))
      if (!player.doglife) {
        val rightCard = controlSuitplayed(trick, player)
        player.removeCard(rightCard)
        TrickControl.playCard(trick, round, rightCard, player)
      } else if (player.currentHand().exists(_.cards.nonEmpty)) {
        val card = PlayerControl.dogplayCard(player, round)
        if (card.isEmpty) {
          ControlHandler.invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
        } else {
          player.removeCard(card.get)
          TrickControl.playCard(trick, round, card.get, player)
        }
      }
    }
    val (winner, finalTrick) = TrickControl.wonTrick(trick, round)
    clearConsole()
    println(printTrick(round))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, winner))
    clearConsole(2)
    playerQueue.resetAndSetStart(winner)
    if (!KnockOutWhist.DEBUG_MODE) ControlHandler.invoke(DelayEvent(3000L))
    finalTrick
  }

  private[control] def controlSuitplayed(trick: Trick, player: Player): Card = {
    var card = PlayerControl.playCard(player)
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
          card = PlayerControl.playCard(player)
        }
      }
    }
    card
  }

  def printMenu(): Try[Int] = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_MENU))
    ControlHandler.invoke(RequestNumberEvent(1, 2))
  }

  def printTrick(round: Round): String = {
    val sb = new StringBuilder()
    sb.append("Current Trick:\n")
    sb.append("Trump-Suit: " + round.trumpSuit + "\n")
    if (round.get_current_trick().isDefined && round.get_current_trick().get.get_first_card().isDefined) {
      sb.append(s"Suit to play: ${round.get_current_trick().get.get_first_card().get.suit}\n")
    }
    for ((card, player) <- round.get_current_trick().get.cards) {
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

  def nextRound(matchImpl: Match): Round = {
    if (MatchControl.isOver(matchImpl)) {
      println(s"The match is over. The winner is ${finalizeMatch(matchImpl).name}.")
      return null
    }
    create_round(matchImpl)
  }

  def nextTrick(roundImpl: Round): Trick = {
    if (RoundControl.isOver(roundImpl)) {
      println("The round is over.")
      return null
    }
    RoundControl.create_trick(roundImpl)
  }

  def create_round(matchImpl: Match): Round = {
    val remainingPlayer = matchImpl.roundlist.isEmpty ? matchImpl.totalplayers |: RoundControl.remainingPlayers(matchImpl.roundlist.last)
    provideCards(matchImpl,remainingPlayer)
    if (matchImpl.roundlist.isEmpty) {
      val random_trumpsuit = CardManager.nextCard().suit
      matchImpl.current_round = Some(new Round(random_trumpsuit, matchImpl, remainingPlayer, true))
    } else {
      val winner = matchImpl.roundlist.last.winner
      val trumpsuit = PlayerControl.pickNextTrumpsuit(winner)

      matchImpl.current_round = Some(new Round(trumpsuit, matchImpl, remainingPlayer, false))
    }
    matchImpl.number_of_cards -= 1
    matchImpl.current_round.get
  }

  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      RoundControl.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }

  private def provideCards(matchImpl: Match, players: List[Player]): Int = {
    if (!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    var hands = 0
    for (player <- players) {
      if (!player.doglife) {
        player.provideHand(CardManager.createHand(matchImpl.number_of_cards))
      } else {
        player.provideHand(CardManager.createHand(1))
      }
      hands += 1
    }
    hands
  }

  def finalizeMatch(matchImpl: Match): Player = {
    if (!isOver(matchImpl)) {
      throw new IllegalStateException("Match is not over yet.")
    }
    RoundControl.remainingPlayers(matchImpl.roundlist.last).head
  }

}
