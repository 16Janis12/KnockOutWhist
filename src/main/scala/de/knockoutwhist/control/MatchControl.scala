package de.knockoutwhist.control

import de.knockoutwhist.control.RoundControl.controlRound
import de.knockoutwhist.events.*
import de.knockoutwhist.events.ERROR_STATUS.{IDENTICAL_NAMES, INVALID_NAME_FORMAT, INVALID_NUMBER_OF_PLAYERS}
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.utils.CustomPlayerQueue

import scala.compiletime.uninitialized
import scala.io.StdIn
import scala.util.Random

object MatchControl {

  var playerQueue: CustomPlayerQueue[AbstractPlayer] = uninitialized //später wieder private machen

  def startMatch(): AbstractPlayer = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_START_MATCH))
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[AbstractPlayer](players, Random.nextInt(players.length))
    controlMatch()
  }

  def enterPlayers(): Array[AbstractPlayer] = {
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
    names.map(s => PlayerFactory.createPlayer(s, HUMAN))
  }

  def controlMatch(): AbstractPlayer = {
    val matchImpl = Match(playerQueue.toList)
    while (!isOver(matchImpl)) {
      controlRound(matchImpl)
    }
    val winner = finalizeMatch(matchImpl)
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

  def finalizeMatch(matchImpl: Match): AbstractPlayer = {
    if (!isOver(matchImpl)) {
      throw new IllegalStateException("Match is not over yet.")
    }
    RoundControl.remainingPlayers(matchImpl.roundlist.last).head
  }

}
