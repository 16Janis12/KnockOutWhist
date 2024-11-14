package de.knockoutwhist.ui.tui

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.control.{ControlHandler, MatchControl}
import de.knockoutwhist.events.ERROR_STATUS.*
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.events.MATCH_STATUS.*
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.events.*
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.player.Player
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent, SimpleEvent}

import scala.annotation.tailrec
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

object TUIMain extends EventListener with UI {


  override def listen[R](event: ReturnableEvent[R]): Option[R] = {
    event match {
      case event: RenderHandEvent =>
        TUICards.renderHandEvent(event.hand, event.showNumbers).foreach(println)
        Some(true)
      case event: ShowTieCardsEvent =>
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
      case event: ShowGlobalStatus =>
        event.status match {
          case SHOW_TIE =>
            println("It's a tie! Let's cut to determine the winner.")
            Some(true)
          case SHOW_TIE_WINNER =>
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Player]) {
              None
            } else {
              println(s"${event.objects.head.asInstanceOf[Player].name} wins the cut!")
              Some(true)
            }
          case SHOW_TIE_TIE =>
            println("It's a tie again! Let's cut again.")
            Some(true)
          case SHOW_START_MATCH =>
            TUIUtil.clearConsole()
            println("Starting a new match...")
            TUIUtil.clearConsole(2)
            Some(true)
          case SHOW_TYPE_PLAYERS =>
            println("Please enter the names of the players, separated by a comma.")
            Some(true)
          case _ => None
        }
      case event: ShowPlayerStatus =>
        val player = event.player
        event.status match {
          case SHOW_TURN =>
            println("It's your turn, " + player.name + ".")
            Some(true)
          case SHOW_PLAY_CARD =>
            println("Which card do you want to play?")
            Some(true)
          case SHOW_DOG_PLAY_CARD =>
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Boolean]) {
              None
            } else {
              println("You are using your dog life. Do you want to play your final card now?")
              if (event.objects.head.asInstanceOf[Boolean]) {
                println("You have to play your final card this round!")
                println("Please enter y to play your final card.")
                Some(true)
              } else {
                println("Please enter y/n to play your final card.")
                Some(true)
              }
            }
          case SHOW_TIE_NUMBERS =>
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Int]) {
              None
            } else {
              println(s"${player.name} enter a number between 1 and ${event.objects.head.asInstanceOf[Int]}.")
              Some(true)
            }
          case SHOW_TRUMPSUIT_OPTIONS =>
            println("Which suit do you want to pick as the next trump suit?")
            println("1: Hearts")
            println("2: Diamonds")
            println("3: Clubs")
            println("4: Spades")
            println()
            Some(true)
          case SHOW_NOT_PLAYED =>
            println(s"Player ${event.player} decided to not play his card")
            Some(true)
          case SHOW_WON_PLAYER_TRICK =>
            println(s"${event.player.name} won the trick.")
            TUIUtil.clearConsole(2)
            Some(true)
          case _ => None
        }
      case event: ShowMatchStatus =>
        event.status match {
          case SHOW_FINISHED =>
            if (event.objects.length != 1 || !event.objects.head.isInstanceOf[Player]) {
              None
            } else {
              TUIUtil.clearConsole()
              println(s"The match is over. The winner is ${event.objects.head.asInstanceOf[Player]}")
              Some(true)
            }
        }
      case event: ShowRoundStatus =>
        event.status match {
          case SHOW_START_ROUND =>
            TUIUtil.clearConsole()
            println(s"Starting a new round. The trump suit is ${event.currentRound.trumpSuit}.")
            TUIUtil.clearConsole(2)
            Some(true)
          case WON_ROUND =>
            if (event.objects.length != 1 || !event.objects.head.isInstanceOf[Player]) {
              None
            } else {
              println(s"${event.objects.head.asInstanceOf[Player].name} won the round.")
              Some(true)
            }
          case PLAYERS_OUT =>
            println("The following players are out of the game:")
            event.currentRound.players_out.foreach(p => {
              println(p.name)
            })
            Some(true)
        }
      case event: ShowErrorStatus =>
        event.status match {
          case INVALID_NUMBER =>
            println("Please enter a valid number.")
            Some(true)
          case NOT_A_NUMBER =>
            println("Please enter a valid number.")
            Some(true)
          case INVALID_INPUT =>
            println("Please enter a valid input")
            Some(true)
          case INVALID_NUMBER_OF_PLAYERS =>
            println("Please enter at least two names.")
            Some(true)
          case IDENTICAL_NAMES =>
            println("Please enter unique names.")
            Some(true)
          case INVALID_NAME_FORMAT =>
            println("Please enter valid names. Those can not be empty, shorter than 2 or longer then 10 characters.")
            Some(true)
          case WRONG_CARD =>
            if(event.objects.length != 1 || !event.objects.head.isInstanceOf[Card]) {
              None
            } else {
              println(f"You have to play a card of suit: ${event.objects.head.asInstanceOf[Card].suit}\n")
              Some(true)
            }
          case _ => None
        }
      case event: RequestNumberEvent =>
        Some(Try {
          val input = readLine()
          val number = input.toInt
          if (number < event.min || number > event.max) {
            throw new IllegalArgumentException(s"Number must be between ${event.min} and ${event.max}")
          }
          number
        })
      case event: RequestCardEvent =>
        Some(Try {
          val card = readLine().toInt - 1
          if (card < 0 || card >= event.hand.cards.length) {
            throw new IllegalArgumentException(s"Number has to be between 1 and ${event.hand.cards.length}")
          } else {
            event.hand.cards(card)
          }
        })
      case event: RequestDogPlayCardEvent =>
        Some(Try {
          val card = readLine()
          if (card.equalsIgnoreCase("y")) {
            Some(event.hand.cards.head)
          } else if (card.equalsIgnoreCase("n") && !event.needs_to_play) {
            None
          } else {
            throw new IllegalArgumentException("Didn't want to play card but had to")
          }
        }
        )
      case event: RequestPickTrumpsuitEvent =>
        Some(Try {
          val suit = readLine().toInt
          suit match {
            case 1 => Suit.Hearts
            case 2 => Suit.Diamonds
            case 3 => Suit.Clubs
            case 4 => Suit.Spades
            case _ => throw IllegalArgumentException("Didn't enter a number between 1 and 4")
          }
        })
      case event: ShowCurrentTrickEvent =>
        TUIUtil.clearConsole()
        val sb = new StringBuilder()
        sb.append("Current Trick:\n")
        sb.append("Trump-Suit: " + event.round.trumpSuit + "\n")
        if (event.trick.get_first_card().isDefined) {
          sb.append(s"Suit to play: ${event.trick.get_first_card().get.suit}\n")
        }
        for ((card, player) <- event.trick.cards) {
          sb.append(s"${player.name} played ${card.toString}\n")
        }
        println(sb.toString())
        Some(true)
      case _ => None
    }
  }
  
  
  object TUICards {
    def renderCardAsString(card: Card): Vector[String] = {
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
    
    private[ui] def cardColour(suit: Suit): String = suit match {
      case Suit.Hearts | Suit.Diamonds => Console.RED
      case Suit.Clubs | Suit.Spades => Console.BLACK
    }
    
    def renderHandEvent(hand: Hand, showNumbers: Boolean): Vector[String] = {
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
  private object TUIUtil {
    def clearConsole(lines: Int = 32): Int = {
      var l = 0
      for (_ <- 0 until lines) {
        println()
        l += 1
      }
      l
    }
  }

  @tailrec
  override def initial: Boolean = {
    TUIUtil.clearConsole()
    println("Welcome to Knockout Whist!")
    println()
    println("Please select an option:")
    println("1. Start a new match")
    println("2. Exit")
    Try{
      readLine().toInt
    } match {
      case Success(value) =>
        value match {
          case 1 =>
            MatchControl.startMatch()
            initial
          case 2 =>
            println("Exiting the game.")
            true
          case _ =>
            ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
            initial
        }
      case Failure(_) =>
        ControlHandler.invoke(ShowErrorStatus(NOT_A_NUMBER))
        initial
    }
  }
}
