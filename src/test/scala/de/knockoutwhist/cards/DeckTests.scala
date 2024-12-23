package de.knockoutwhist.cards

import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

class DeckTests extends AnyWordSpec with Matchers{

  "A deck" should {
    TestUtil.disableDelay()
    "not be empty" in {
      CardManager.cardContainer must not be empty
    }
    "have 52 cards" in {
      CardManager.cardContainer must have size 52
    }
    "contain 13 cards of spades" in {
      CardManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
    }
    "contain 13 cards of hearts" in {
      CardManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
    }
    "contain 13 cards of diamonds" in {
      CardManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
    }
    "contain 13 cards of clubs" in {
      CardManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
    }
    "have cards in a different order after shuffling it" in {
      val originalDeck = List(CardManager.cardContainer)
      CardManager.shuffleAndReset()
      val shuffledDeck = CardManager.cardContainer
      shuffledDeck should not equal originalDeck
    }
    "provide cards" in {
      val nextCard = CardManager.nextCard()
      nextCard should not be null
    }
    "provide different cards" in {
      val nextCard = CardManager.nextCard()
      val nextCard2 = CardManager.nextCard()
      nextCard should not equal nextCard2
    }
    "supply a hand of 7 cards for the first round" in {
      val hand = CardManager.createHand()
      hand.cards should have size 7
    }
    "supply a hand of 2 cards" in {
      val hand = CardManager.createHand(2)
      hand.cards should have size 2
    }
    "throw an exception if you request more then 52 cards without shuffling" in {
      assertThrows[IndexOutOfBoundsException] {
        for (_ <- 1 to 53) {
          CardManager.nextCard()
        }
      }
    }
  }


}
