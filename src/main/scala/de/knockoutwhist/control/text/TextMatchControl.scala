package de.knockoutwhist.control.text

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Player
import de.knockoutwhist.control.{MatchControl, PlayerControl}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.CustomPlayerQueue

import scala.io.StdIn
import scala.compiletime.uninitialized
import scala.util.Random

object TextMatchControl extends MatchControl {

  private var playerQueue: CustomPlayerQueue[Player] = uninitialized

  override def initial(): Boolean = {
    println("Welcome to Knockout Whist!")
    start()
    true
  }

  override def start(): Unit = {
    while(true) { //Main Gameplay Loop
      val input = printMenu()
      input match {
        case "1" =>
          startMatch()
        case "2" =>
          println("Exiting the game.")
          return
        case _ =>
          println("Invalid input. Please try again.")
      }
    }
  }

  private[control] def startMatch(): Player = {
    clearConsole()
    println("Starting a new match.")
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[Player](players, Random.nextInt(players.length))
    clearConsole()
    controlMatch()
  }

  private[control] def enterPlayers(): Array[Player] = {
    println("Please enter the names of the players, separated by a comma.")
    val names = StdIn.readLine().split(",")
    if(names.length < 2) {
      println("Please enter at least two names.")
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
    println(s"The match is over. The winner is ${matchImpl.finalizeMatch().name}.")
    matchImpl.finalizeMatch()
  }

  private[control] def controlRound(matchImpl: Match): Round = {
    val roundImpl = nextRound(matchImpl)
    clearConsole(10)
    println(s"Starting a new round. The trump suit is ${roundImpl.trumpSuit}.")
    clearConsole(2)
    while (!roundImpl.isOver) {
      controlTrick(roundImpl)
    }
    val (roundWinner, finalRound) = roundImpl.finalizeRound()
    println(s"${roundWinner.name} won the round.")
    if(!KnockOutWhist.DEBUG_MODE) Thread.sleep(5000L)
    if(finalRound.players_out != null) {
      println("The following players are out of the game:")
      finalRound.players_out.foreach(p => {
        println(p.name)
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
      val card = playerControl.playCard(player)
      player.removeCard(card)
      trick.playCard(card, player)
    }
    val (winner, finalTrick) = trick.wonTrick()
    clearConsole()
    println(printTrick(round))
    println(s"${winner.name} won the trick.")
    clearConsole(2)
    playerQueue.resetAndSetStart(winner)
    if(!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    finalTrick
  }

  private[control] def printMenu(): String = {
    println("Please select an option:")
    println("1. Start a new match")
    println("2. Exit")
    StdIn.readLine()
  }

  private[control] def printTrick(round: Round): String = {
    val sb = new StringBuilder()
    sb.append("Current Trick:\n")
    sb.append("Trump-Suit: " + round.trumpSuit + "\n")
    for((card, player) <- round.get_current_trick().cards) {
      sb.append(s"${player.name} played ${card.toString}\n")
    }
    sb.toString()
  }

  private def clearConsole(lines: Int = 32): Int = {
    var l = 0
    for(_ <- 0 until lines) {
      println()
      l += 1
    }
    l
  }

  override def playerControl: PlayerControl = {
    TextPlayerControl
  }

  override def nextRound(matchImpl: Match): Round = {
    if(matchImpl.isOver) {
      println(s"The match is over. The winner is ${matchImpl.finalizeMatch().name}.")
      return null
    }
    matchImpl.create_round()
  }

  override def nextTrick(roundImpl: Round): Trick = {
    if(roundImpl.isOver) {
      println("The round is over.")
      return null
    }
    roundImpl.create_trick()
  }



}

