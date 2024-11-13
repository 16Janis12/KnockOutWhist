package de.knockoutwhist.tui

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.events.ERROR_STATUS.*
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.*
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.RequestNumberEvent
import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent, SimpleEvent}

import scala.io.StdIn.readLine
import scala.util.Try

object TUIMain extends EventListener {

  override def listen[R](event: ReturnableEvent[R]): Option[R] = {
    event match {
      case event: RenderHandEvent => {
        TUICards.renderHandEvent(event.hand, event.showNumbers).foreach(println)
        Some(true)
      }
      case event: ShowTieCardsEvent => {
        val a: Array[String] = Array("", "", "", "", "", "", "", "")
        for ((player,card) <- event.card) {
          val playerNameLength = player.name.length
          a(0) += " " + player.name + ":" + (" " * (playerNameLength - 1))
          val rendered = TUICards.renderCardAsString(card)
          a(1) += " " + rendered(0)
          a(2) += " " + rendered(1)
          a(3) += " " + rendered(2)
          a(4) += " " + rendered(3)
          a(5) += " " + rendered(4)
          a(6) += " " + rendered(5)
          a(7) += " " + rendered(6)
        }
        a.foreach(println)
        Some(true)
      }
      case event: ShowGlobalStatus => {
        event.status match {
          case SHOW_TIE_WINNER => {
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Player]) {
              None
            } else {
              println(s"${event.objects.head.asInstanceOf[Player].name} wins the cut!")
              Some(true)
            }
          }
          case SHOW_TIE_TIE => {
            println("It's a tie again! Let's cut again.")
            Some(true)
          }
          case _ => None
        }
      }
      case event: ShowPlayerStatus => {
        val player = event.player
        event.status match {
          case SHOW_TIE_NUMBERS => {
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Int]) {
              None
            } else {
              println(s"${player.name} enter a number between 1 and ${event.objects.head.asInstanceOf[Int]}.")
              Some(true)
            }
          }
          case _ => None
        }
      }
      case event: ShowErrorStatus => {
        event.status match {
          case INVALID_NUMBER => {
            println("Please enter a valid number.")
            Some(true)
          }
          case NOT_A_NUMBER => {
            println("Please enter a valid number.")
            Some(true)
          }
          case _ => None
        }
      }
      case event: RequestNumberEvent => {
        Some(Try {
          val input = readLine()
          val number = input.toInt
          if (number < event.min || number > event.max) {
            throw new IllegalArgumentException(s"Number must be between ${event.min} and ${event.max}")
          }
          number
        })
      }
      case _ => None
    }
  }
  
  
  private object TUICards {
    private[tui] def renderCardAsString(card: Card): Vector[String] = {
      if (card.cardValue == CardValue.Ten) {
        return Vector(
          s"┌─────────┐",
          s"│${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}       │",
          "│         │",
          s"│    ${cardColour(card.suit)}${Console.BOLD}${card.suit.cardType()}${Console.RESET}    │",
          "│         │",
          s"│       ${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}│",
          s"└─────────┘"
        )
      }
      Vector(
        s"┌─────────┐",
        s"│${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}        │",
        "│         │",
        s"│    ${cardColour(card.suit)}${Console.BOLD}${card.suit.cardType()}${Console.RESET}    │",
        "│         │",
        s"│        ${cardColour(card.suit)}${Console.BOLD}${card.cardValue.cardType()}${Console.RESET}│",
        s"└─────────┘"
      )
    }
    
    private[tui] def cardColour(suit: Suit): String = suit match {
      case Suit.Hearts | Suit.Diamonds => Console.RED
      case Suit.Clubs | Suit.Spades => Console.BLACK
    }
    
    private[tui] def renderHandEvent(hand: Hand, showNumbers: Boolean): Vector[String] = {
      val cardStrings = hand.cards.map(TUICards.renderCardAsString)
      var zipped = cardStrings.transpose
      if (showNumbers) zipped = {
        List.tabulate(zipped.length) { i =>
          s"     ${i + 1}     "
        }
      } :: zipped
      zipped.map(_.mkString(" ")).toVector
    }
  }



}
