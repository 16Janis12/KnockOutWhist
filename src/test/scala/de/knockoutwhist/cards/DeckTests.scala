package de.knockoutwhist.cards

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

class DeckTests extends AnyWordSpec with Matchers{

  "A deck" should {
    TestUtil.disableDelay()
    "not be empty" in {
      KnockOutWhist.config.cardManager.cardContainer must not be empty
    }
    "have 52 cards" in {
      KnockOutWhist.config.cardManager.cardContainer must have size 52
    }
    "contain 13 cards of spades" in {
      KnockOutWhist.config.cardManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
    }
    "contain 13 cards of hearts" in {
      KnockOutWhist.config.cardManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
    }
    "contain 13 cards of diamonds" in {
      KnockOutWhist.config.cardManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
    }
    "contain 13 cards of clubs" in {
      KnockOutWhist.config.cardManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
    }
    "have cards in a different order after shuffling it" in {
      val originalDeck = List(KnockOutWhist.config.cardManager)
      val cardManager = KnockOutWhist.config.cardManager
      cardManager.shuffleAndReset()
      val shuffledDeck = KnockOutWhist.config.cardManager
      shuffledDeck should not equal originalDeck
    }
    "provide cards" in {
      val cardManager = KnockOutWhist.config.cardManager
      val nextCard = cardManager.nextCard()
      nextCard should not be null
    }
    "provide different cards" in {
      val cardManager = KnockOutWhist.config.cardManager
      val nextCard = cardManager.nextCard()
      val nextCard2 = cardManager.nextCard()
      nextCard should not equal nextCard2
    }
    "supply a hand of 7 cards for the first round" in {
      val cardManager = KnockOutWhist.config.cardManager
      val hand = cardManager.createHand()
      hand.cards should have size 7
    }
    "supply a hand of 2 cards" in {
      val cardManager = KnockOutWhist.config.cardManager
      val hand = cardManager.createHand(2)
      hand.cards should have size 2
    }
    "throw an exception if you request more then 52 cards without shuffling" in {
      val cardManager = KnockOutWhist.config.cardManager
      assertThrows[IndexOutOfBoundsException] {
        for (_ <- 1 to 53) {
          cardManager.nextCard()
        }
      }
    }
  }


}
