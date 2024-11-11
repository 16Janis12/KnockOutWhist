package de.knockoutwhist.tui

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.events.RenderHandEvent
import de.knockoutwhist.utils.events.{Event, EventListener}

object TUIMain extends EventListener {

  override def listen(event: Event): Boolean = {
    event match {
      case RenderHandEvent(hand, showNumbers) => {
        TUICards.renderHandEvent(hand, showNumbers).foreach(println)
        true
      }
      case _ => false
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
