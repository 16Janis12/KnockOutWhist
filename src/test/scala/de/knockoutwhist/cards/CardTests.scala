package de.knockoutwhist.cards

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardTests extends AnyWordSpec with Matchers{

  "Knock Out Whist" should {
    "The card container can't be empty" in {
      CardManager.cardContainer must not be empty
    }
    "A deck should have 52 cards" in {
      CardManager.cardContainer must have size 52
    }
    "A deck should have 13 cards of each suit" in {
      "13 cards of Spades" in {
        CardManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
      }
      "13 cards of Hearts" in {
        CardManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
      }
      "13 cards of Diamonds" in {
        CardManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
      }
      "13 cards of Clubs" in {
        CardManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
      }
    }
    "A deck should have 4 cards of each value" in {
      "4 cards of Two" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Two) mustBe 4
      }
      "4 cards of Three" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Three) mustBe 4
      }
      "4 cards of Four" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Four) mustBe 4
      }
      "4 cards of Five" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Five) mustBe 4
      }
      "4 cards of Six" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Six) mustBe 4
      }
      "4 cards of Seven" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Seven) mustBe 4
      }
      "4 cards of Eight" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Eight) mustBe 4
      }
      "4 cards of Nine" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Nine) mustBe 4
      }
      "4 cards of Ten" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Ten) mustBe 4
      }
      "4 cards of Jack" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Jack) mustBe 4
      }
      "4 cards of Queen" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Queen) mustBe 4
      }
      "4 cards of King" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.King) mustBe 4
      }
      "4 cards of Ace" in {
        CardManager.cardContainer.count(_.cardValue == CardValue.Ace) mustBe 4
      }
    }
    "Check if shuffleAndReset shuffles the deck" in {
      val originalDeck = CardManager.cardContainer.clone()
      CardManager.shuffleAndReset()
      CardManager.cardContainer must not equal originalDeck
    }
  }

}
