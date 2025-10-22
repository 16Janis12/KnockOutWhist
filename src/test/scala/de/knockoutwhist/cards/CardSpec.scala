package de.knockoutwhist.cards

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec with Matchers {

  "Card" should {

    "have correct string representation" in {
      val card = Card(CardValue.Ace, Suit.Hearts)
      card.toString shouldEqual "Ace of Hearts"
    }

    "be equal when card value and suit are the same" in {
      val card1 = Card(CardValue.King, Suit.Spades)
      val card2 = Card(CardValue.King, Suit.Spades)
      card1 shouldEqual card2
    }

    "not be equal when card value or suit differ" in {
      val card1 = Card(CardValue.Queen, Suit.Diamonds)
      val card2 = Card(CardValue.Jack, Suit.Diamonds)
      val card3 = Card(CardValue.Queen, Suit.Clubs)
      card1 should not equal card2
      card1 should not equal card3
    }

    "canEqual should return true for Card instances" in {
      val card = Card(CardValue.Ten, Suit.Hearts)
      card.canEqual(card) shouldBe true
    }

    "canEqual should return false for non-Card instances" in {
      val card = Card(CardValue.Ten, Suit.Hearts)
      card.canEqual("Not a card") shouldBe false
    }

    "equals should return false for non-Card instances" in {
      val card = Card(CardValue.Ten, Suit.Hearts)
      card.equals("Not a card") shouldBe false
    }
  }

  "Card Value" should {

    "return correct identifier" in {
      CardValue.Ten.cardType() shouldEqual "10"
      CardValue.Ace.cardType() shouldEqual "A"
    }

  }

  "Suit" should {

    "return correct identifier" in {
      Suit.Spades.cardType() shouldEqual "♠"
      Suit.Hearts.cardType() shouldEqual "♥"
    }

  }

}
