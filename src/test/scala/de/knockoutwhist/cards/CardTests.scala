package de.knockoutwhist.cards
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.tui.TUIMain.TUICards.renderCardAsString
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardTests extends AnyWordSpec with Matchers{

  "A card" should {
    TestUtil.disableDelay()
    "be displayed with correct value and Suit" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val e = "Ace of Spades"
      card.toString.equals(e) shouldBe true
    }
    "can be rendered" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val expectedResult = Vector[String](
        "┌─────────┐",
        s"│${Console.BLACK}${Console.BOLD}A${Console.RESET}        │",
        "│         │",
        s"│    ${Console.BLACK}${Console.BOLD}♠${Console.RESET}    │",
        "│         │",
        s"│        ${Console.BLACK}${Console.BOLD}A${Console.RESET}│",
        "└─────────┘"
      )
      renderCardAsString(Card(CardValue.Ace, Suit.Spades)) shouldBe expectedResult
    }
    "can be rendered for CardValue Ten" in {
      val card = Card(CardValue.Ten, Suit.Spades)
      val expectedResult = Vector[String](
        "┌─────────┐",
        s"│${Console.BLACK}${Console.BOLD}10${Console.RESET}       │",
        "│         │",
        s"│    ${Console.BLACK}${Console.BOLD}♠${Console.RESET}    │",
        "│         │",
        s"│       ${Console.BLACK}${Console.BOLD}10${Console.RESET}│",
        "└─────────┘"
      )
      renderCardAsString(Card(CardValue.Ten, Suit.Spades)) shouldBe expectedResult
    }
    "be able to reset the order" in {
      val cardManager = KnockOutWhist.config.cardManager
      cardManager.shuffleAndReset()
      cardManager.resetOrder()
      val card = cardManager.nextCard()
      card.suit shouldBe Suit.Spades
      card.cardValue shouldBe CardValue.Two
    }
  }

}
