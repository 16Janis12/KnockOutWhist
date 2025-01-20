package de.knockoutwhist.cards

import de.knockoutwhist.cards.stub.StubCardManager
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec

class StubCardManagerTests extends AnyWordSpec with Matchers{

  "A StubCardManager" should {
    TestUtil.disableDelay()
    "return an empty list when calling cardContainer" in {
      StubCardManager.cardContainer shouldBe List()
    }
    "not throw an exception while shuffling" in {
      StubCardManager.shuffleAndReset()
    }
    "not throw an exception while resetting the order" in {
      StubCardManager.resetOrder()
    }
    "return the Ace of Clubs as the next Card in the deck" in {
      StubCardManager.nextCard() shouldBe Card(CardValue.Ace, Suit.Clubs)
    }
    "return a hand of 1 card which is the Ace of Clubs when calling createHand" in {
      StubCardManager.createHand(4) shouldBe Hand(List(Card(CardValue.Ace, Suit.Clubs)))
    }
  }
}
