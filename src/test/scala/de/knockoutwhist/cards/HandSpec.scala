package de.knockoutwhist.cards

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class HandSpec extends AnyWordSpec with Matchers {

  "Hand" should {

    "remove the specified card" in {
      val c1 = Card(CardValue.Ace, Suit.Hearts)
      val c2 = Card(CardValue.Two, Suit.Spades)
      val hand = Hand(List(c1, c2))

      val after = hand.removeCard(c1)
      after shouldEqual Hand(List(c2))
    }

    "return same hand when removing a card not present" in {
      val c1 = Card(CardValue.King, Suit.Clubs)
      val c2 = Card(CardValue.Queen, Suit.Diamonds)
      val hand = Hand(List(c1))
      val after = hand.removeCard(c2)
      after shouldEqual hand
    }

    "hasSuit returns true when a card with the suit is present" in {
      val c = Card(CardValue.King, Suit.Hearts)
      val hand = Hand(List(c))
      hand.hasSuit(Suit.Hearts) shouldBe true
      hand.hasSuit(Suit.Spades) shouldBe false
    }

    "hasValue returns true when a card with the value is present" in {
      val c = Card(CardValue.Queen, Suit.Clubs)
      val hand = Hand(List(c))
      hand.hasValue(CardValue.Queen) shouldBe true
      hand.hasValue(CardValue.Ten) shouldBe false
    }

    "hasTrumpSuit returns true when a trump suit card is present" in {
      val trump = Suit.Spades
      val c = Card(CardValue.Jack, trump)
      val hand = Hand(List(c))
      hand.hasTrumpSuit(trump) shouldBe true
      hand.hasTrumpSuit(Suit.Hearts) shouldBe false
    }

    "removeCard removes all equal cards (duplicates)" in {
      val c = Card(CardValue.Ten, Suit.Clubs)
      val hand = Hand(List(c, c, Card(CardValue.Ace, Suit.Spades)))
      val after = hand.removeCard(c)
      after shouldEqual Hand(List(Card(CardValue.Ace, Suit.Spades)))
    }
  }
}

