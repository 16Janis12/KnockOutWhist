package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager}
import de.knockoutwhist.control.RoundControl.controlRound
import de.knockoutwhist.events.*
import de.knockoutwhist.events.ERROR_STATUS.{IDENTICAL_NAMES, INVALID_NAME_FORMAT, INVALID_NUMBER_OF_PLAYERS, WRONG_CARD}
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*

import scala.compiletime.uninitialized
import scala.io.StdIn
import scala.util.Random

object MatchControl {

  private[control] var playerQueue: CustomPlayerQueue[Player] = uninitialized

  def startMatch(): Player = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_START_MATCH))
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[Player](players, Random.nextInt(players.length))
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
    val winner = finalizeMatch(matchImpl)
    val playerwinner = winner.name
    ControlHandler.invoke(ShowGlobalStatus(SHOW_FINISHED_MATCH, winner))
    winner
  }
  
  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      RoundControl.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }

  def finalizeMatch(matchImpl: Match): Player = {
    if (!isOver(matchImpl)) {
      throw new IllegalStateException("Match is not over yet.")
    }
    RoundControl.remainingPlayers(matchImpl.roundlist.last).head
  }

}
