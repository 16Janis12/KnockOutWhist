package de.knockoutwhist.cards

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

class DeckTests extends AnyWordSpec with Matchers{

  "A deck" should {
    TestUtil.disableDelay()
    "not be empty" in {
      CardBaseManager.cardContainer must not be empty
    }
    "have 52 cards" in {
      CardBaseManager.cardContainer must have size 52
    }
    "contain 13 cards of spades" in {
      CardBaseManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
    }
    "contain 13 cards of hearts" in {
      CardBaseManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
    }
    "contain 13 cards of diamonds" in {
      CardBaseManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
    }
    "contain 13 cards of clubs" in {
      CardBaseManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
    }
    "have cards in a different order after shuffling it" in {
      val originalDeck = List(CardBaseManager.cardContainer)
      CardBaseManager.shuffleAndReset()
      val shuffledDeck = CardBaseManager.cardContainer
      shuffledDeck should not equal originalDeck
    }
    "provide cards" in {
      val nextCard = CardBaseManager.nextCard()
      nextCard should not be null
    }
    "provide different cards" in {
      val nextCard = CardBaseManager.nextCard()
      val nextCard2 = CardBaseManager.nextCard()
      nextCard should not equal nextCard2
    }
    "supply a hand of 7 cards for the first round" in {
      val hand = CardBaseManager.createHand()
      hand.cards should have size 7
    }
    "supply a hand of 2 cards" in {
      val hand = CardBaseManager.createHand(2)
      hand.cards should have size 2
    }
    "throw an exception if you request more then 52 cards without shuffling" in {
      assertThrows[IndexOutOfBoundsException] {
        for (_ <- 1 to 53) {
          CardBaseManager.nextCard()
        }
      }
    }
  }


}
