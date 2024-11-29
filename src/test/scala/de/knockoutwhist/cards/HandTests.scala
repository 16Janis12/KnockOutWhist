package de.knockoutwhist.cards

import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.tui.TUIMain.TUICards.renderHandEvent
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer

class HandTests extends AnyWordSpec with Matchers {

  "The hand" should {
    TestUtil.disableDelay()
    "be able to remove cards from its hand" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      val removedhand = hand.removeCard(Card(CardValue.Ace, Suit.Spades))
      removedhand.cards should have size 0
    }
    "be able to see, if he has a certain suit" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasSuit(Suit.Spades) shouldBe true
    }
    "be able to see, if he has a certain value" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasValue(CardValue.Ace) shouldBe true
    }
    "be able to see, if he has a card of Trumpsuit" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasTrumpSuit(Suit.Spades) shouldBe true
    }
    "be able to render his hand" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      handholder.addOne(Card(CardValue.Queen, Suit.Diamonds))
      val hand = Hand(handholder.toList)
      val expectedResult = List(
        "┌─────────┐ ┌─────────┐",
        s"│${Console.BLACK}${Console.BOLD}A${Console.RESET}        │ │${Console.RED}${Console.BOLD}Q${Console.RESET}        │",
        "│         │ │         │",
        s"│    ${Console.BLACK}${Console.BOLD}♠${Console.RESET}    │ │    ${Console.RED}${Console.BOLD}♦${Console.RESET}    │",
        "│         │ │         │",
        s"│        ${Console.BLACK}${Console.BOLD}A${Console.RESET}│ │        ${Console.RED}${Console.BOLD}Q${Console.RESET}│",
        "└─────────┘ └─────────┘"
      )
      renderHandEvent(Hand(List(Card(CardValue.Ace, Suit.Spades), Card(CardValue.Queen, Suit.Diamonds))), false) shouldBe expectedResult
    }
  }

}
