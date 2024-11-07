package de.knockoutwhist.control.text

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import util.control.Breaks.*
import java.io.IOException

object TextPlayerControl extends PlayerControl {

  override def playCard(player: Player): Card = {
    println("It's your turn, " + player.name + ".")
    if(!KnockOutWhist.DEBUG_MODE) Thread.sleep(3000L)
    println("Which card do you want to play?")
    showCards(player)
    try {
      val card = readLine().toInt-1
      val handCard = player.currentHand()
      if (handCard.isEmpty) {
        println("You don't have any cards.")
        throw new IllegalStateException("Trying to play a card without any cards.")
      } else if(card < 0 || card >= handCard.get.cards.length) {
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
    if(round.dogNeedsToPlay) {
      println("You have to play your final card this round!")
      println("Please enter y to play your final card.")
    }else {
      println("Please enter y/n to play your final card.")
    }

    showCards(player)
    try {
      val card = readLine()
      val handCard = player.currentHand()
      if (handCard.isEmpty) {
        println("You don't have any cards.")
        throw new IllegalStateException("Trying to play a card without any cards.")
      } else if(card.equalsIgnoreCase("y")) {
        Some(handCard.get.cards.head)
      } else if (card.equalsIgnoreCase("n") && !round.dogNeedsToPlay) {
        None
      } else {
        println("Please enter y or n to play your final card.")
        dogplayCard(player, round)
      }

    } catch {
      case e: IOException => println(s"An I/O error occurred: ${e.getMessage}")
        None
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
        println(s"${player.name} enter a number between 1 and $remaining.")
        try {
          val selected = readLine().toInt - 1
          if (selected >= 0 && selected < remaining) {
            selCard = CardManager.cardContainer(currentStep + selected)
            cut.put(player, selCard)
            currentStep += selected + 1
            remaining -= selected
          } else {
            println("Please enter a valid number.")
          }
        } catch {
          case e: NumberFormatException =>
            println("Please enter a valid number.")
        }
      }
    }
    println("The cards are:")
    val a: Array[String] = Array("", "", "", "", "", "", "", "")
    for ((player, card) <- cut) {
      val playerNameLength = player.name.length
      a(0) += " " + player.name + ":" + (" " * (playerNameLength - 1))
      val rendered = card.renderAsString()
      a(1) += " " + rendered(0)
      a(2) += " " + rendered(1)
      a(3) += " " + rendered(2)
      a(4) += " " + rendered(3)
      a(5) += " " + rendered(4)
      a(6) += " " + rendered(5)
      a(7) += " " + rendered(6)
    }
    a.foreach(println)

    var currentHighest: Card = null
    val winner: ListBuffer[Player] = ListBuffer()
    for ((player, card) <- cut) {
      breakable {
        if (currentHighest == null) {
          currentHighest = card
          winner += player
          break
        }
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
      println(s"${winner.head.name} wins the cut!")
      return winner.head
    }
    println("It's a tie again! Let's cut again.")
    determineWinnerTieText(winner.toList, false)
  }

  override def pickNextTrumpsuit(player: Player): Suit = {
    println("Which suit do you want to pick as the next trump suit?")
    println("1: Hearts")
    println("2: Diamonds")
    println("3: Clubs")
    println("4: Spades")
    println()

    player.currentHand().get.renderAsString().foreach(println)

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

  override def showCards(player: Player): Boolean = {
    val hand = player.currentHand()
    if (hand.isEmpty) {
      println("You don't have any cards.")
      return false
    }
    println("Your cards:")
    var rendered = hand.get.renderAsString()
    rendered ::= {
      var s = ""
      for (i <- hand.get.cards.indices) {
        s += s"     ${i+1}     " + " "
      }
      s
    }
    rendered.foreach(println)
    true
  }

  override def showWon(player: Player, round: Round): String = {
    s"${player} won this round. ${round.players_in} played in this round, ${round.players_out} got knocked out.\n${round.trumpSuit} was trumpsuit."
  }

}
