package de.knockoutwhist.control.text

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
          clearConsole()
          println("Starting a new match.")
          println("Please enter the names of the players, separated by a comma.")
          val names = StdIn.readLine().split(",")
          if(names.length < 2) {
            println("Please enter at least two names.")
            return
          }
          playerQueue = CustomPlayerQueue[Player](names.map(s => Player(s)), Random.nextInt(names.length))
          clearConsole()
          val matchImpl = Match(playerQueue.toList)
          while(!matchImpl.isOver) {
            val roundImpl = nextRound(matchImpl)
            clearConsole(10)
            println(s"Starting a new round. The trump suit is ${roundImpl.trumpSuit}.")
            clearConsole(2)
            while(!roundImpl.isOver) {
              val trickImpl = nextTrick(roundImpl)
              for(player <- playerQueue) {
                clearConsole()
                println(printTrick(roundImpl))
                val card = playerControl.playCard(player)
                player.removeCard(card)
                trickImpl.playCard(card, player)
              }
              val (winner, finalTrick) = trickImpl.wonTrick()
              clearConsole()
              println(printTrick(roundImpl))
              println(s"${winner.name} won the trick.")
              clearConsole(2)
              playerQueue.resetAndSetStart(winner)
              Thread.sleep(3000L)
            }
            val (roundWinner, finalRound) = roundImpl.finalizeRound()
            println(s"${roundWinner.name} won the round.")
            Thread.sleep(5000L)
            playerQueue.resetAndSetStart(roundWinner)
          }
        case "2" =>
          println("Exiting the game.")
          return
        case _ =>
          println("Invalid input. Please try again.")
      }
    }
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
    if(round.firstRound && round.get_tricks().isEmpty) {
      sb.append(s"First card: ${round.get_current_trick().get_first_card().get.toString}\n")
    }
    for((card, player) <- round.get_current_trick().cards) {
      sb.append(s"${player.name} played ${card.toString}\n")
    }
    sb.toString()
  }

  private def clearConsole(lines: Int = 32): Unit = {
    for(_ <- 0 until lines) {
      println()
    }
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

