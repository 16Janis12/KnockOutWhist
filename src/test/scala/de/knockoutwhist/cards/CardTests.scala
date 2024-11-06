package de.knockoutwhist.cards

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardTests extends AnyWordSpec with Matchers{

  "A card" should {
    "be displayed with correct value and Suit" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val e = "Ace of Spades"
      card.toString.equals(e) shouldBe true
    }
    "can be rendered" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val expectedResult = Vector[String](
        "┌─────────┐",
        "│A        │",
        "│         │",
        "│    ♠    │",
        "│         │",
        "│        A│",
        "└─────────┘"
      )
      card.renderAsString() shouldBe expectedResult
    }
    "can be rendered for CardValue Ten" in {
      val card = Card(CardValue.Ten, Suit.Spades)
      val expectedResult = Vector[String](
        "┌─────────┐",
        "│10       │",
        "│         │",
        "│    ♠    │",
        "│         │",
        "│       10│",
        "└─────────┘"
      )
      card.renderAsString() shouldBe expectedResult
    }
  }

}
