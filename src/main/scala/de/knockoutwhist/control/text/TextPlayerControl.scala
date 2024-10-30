package de.knockoutwhist.control.text

import de.knockoutwhist.cards.{Card, CardManager, Player}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.rounds.Round

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.StdIn.readLine
import util.control.Breaks.*

object TextPlayerControl extends PlayerControl {

  override def playCard(player: Player): Card = {
    println("It's your turn, " + player.name + ". \nWhich card do you want to play?")
    showCards(player)
    try {
      val card = readLine().toInt
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

  override def determineWinnerTie(players: List[Player], tieMessage: Boolean = true): Player = {
    //CardManager.shuffleAndReset()
    if(tieMessage) println("It's a tie! Let's cut to determine the winner.")
    var currentStep = 0
    var remaining = CardManager.cardContainer.size-(players.length-1)
    val cut: mutable.HashMap[Player, Card] = mutable.HashMap()
    for(player <- players) {
      var selCard: Card = null
      while(selCard == null) {
        println(s"${player.name} enter a number between 1 and $remaining.")
        try {
          val selected = readLine().toInt - 1
          if(selected >= 0 && selected < remaining) {
            selCard = CardManager.cardContainer(currentStep + selected)
            cut.put(player, selCard)
            currentStep = currentStep + selected + 1
            remaining = remaining - selected
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
    val a:Array[String] = Array("", "", "", "", "", "", "", "")
    for((player, card) <- cut) {
      a(0) = a(0) + s" ${player.name}:"
      val rendered = card.renderAsString()
      a(1) = a(1) + " " + rendered(0)
      a(2) = a(2) + " " + rendered(1)
      a(3) = a(3) + " " + rendered(2)
      a(4) = a(4) + " " + rendered(3)
      a(5) = a(5) + " " + rendered(4)
      a(6) = a(6) + " " + rendered(5)
      a(7) = a(7) + " " + rendered(6)
    }
    a.foreach(println)

    var currentHighest:Card = null
    val winner: ListBuffer[Player] = ListBuffer()
    for((player, card) <- cut) {
      breakable {
        if (currentHighest == null) {
          currentHighest = card
          winner += player
          break
        }
        val compared = card.cardValue.ordinal.compareTo(currentHighest.cardValue.ordinal)
        if(compared > 0) {
          currentHighest = card
          winner.clear()
          winner += player
        } else if(compared == 0) {
          winner += player
        }
      }
    }
    if(winner.size == 1) {
      println(s"${winner.head.name} wins the cut!")
      return winner.head
    }
    println("It's a tie again! Let's cut again.")
    determineWinnerTie(winner.toList, false)
  }

  override def showCards(player: Player): Boolean = ???

  override def showWon(player: Player, round: Round): Int = ???

}
