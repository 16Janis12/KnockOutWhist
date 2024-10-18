package de.knockoutwhist.cards

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.should
import org.scalatest.wordspec.AnyWordSpec

class DeckTests extends AnyWordSpec with Matchers{

  "A deck" should {
    "The card container can't be empty" in {
      CardManager.cardContainer must not be empty
    }
    "A deck should have 52 cards" in {
      CardManager.cardContainer must have size 52
    }
    "There are 13 cards of spades" in {
      CardManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
    }
    "There are 13 cards of hearts" in {
      CardManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
    }
    "There are 13 cards of diamonds" in {
      CardManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
    }
    "There are 13 cards of clubs" in {
      CardManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
    }
    "Check if shuffleAndReset shuffles the deck" in {
      val originalDeck = List(CardManager.cardContainer)
      CardManager.shuffleAndReset()
      val shuffledDeck = CardManager.cardContainer
      shuffledDeck should not equal originalDeck
    }
    "Check if the nextCard method returns a card" in {
      val nextCard = CardManager.nextCard()
      nextCard should not be null
    }
    "Grab 2 cards from the deck" in {
      val nextCard = CardManager.nextCard()
      val nextCard2 = CardManager.nextCard()
      nextCard should not equal nextCard2
    }
  }

}
