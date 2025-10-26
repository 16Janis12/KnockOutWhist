package de.knockoutwhist.cards.base

import de.knockoutwhist.cards.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CardBaseManagerSpec extends AnyWordSpec with Matchers {

  "CardBaseManager" should {

    "initialize with 52 cards in canonical order" in {
      val mgr = new CardBaseManager()
      mgr.cardContainer.size shouldBe 52
      mgr.currentIndx shouldBe 0
      mgr.remainingCards shouldBe 52
      mgr.cardContainer.head shouldBe Card(CardValue.Two, Suit.Spades)
    }

    "createHand with default size 7 and advance index" in {
      val mgr = new CardBaseManager()
      val hand = mgr.createHand()
      hand.cards.size shouldBe 7
      mgr.currentIndx shouldBe 7
      mgr.remainingCards shouldBe 45
    }

    "createHand with custom amount" in {
      val mgr = new CardBaseManager()
      val hand = mgr.createHand(5)
      hand.cards.size shouldBe 5
      mgr.currentIndx shouldBe 5
    }

    "removeCards returns amount+1 cards (current implementation uses inclusive loop)" in {
      val mgr = new CardBaseManager()
      val removed = mgr.removeCards(2)
      removed.size shouldBe 3
      mgr.currentIndx shouldBe 3
    }

    "grabSpecificCard returns a matching card without changing index" in {
      val mgr = new CardBaseManager()
      val beforeIdx = mgr.currentIndx
      val desired = Card(CardValue.Ace, Suit.Clubs)
      val found = mgr.grabSpecificCard(desired)
      found shouldBe desired
      mgr.currentIndx shouldBe beforeIdx
    }

    "shuffleAndReset sets index to 0 and resetOrder restores canonical order" in {
      val mgr = new CardBaseManager()
      val originalHead = mgr.cardContainer.head
      mgr.shuffleAndReset()
      mgr.currentIndx shouldBe 0
      mgr.cardContainer.size shouldBe 52
      mgr.resetOrder()
      mgr.cardContainer.head shouldBe Card(CardValue.Two, Suit.Spades)
      mgr.currentIndx shouldBe 0
    }

    "nextCard throws IndexOutOfBoundsException when called at last index (per current implementation)" in {
      val mgr = new CardBaseManager()
      mgr.setState(mgr.cardContainer, 51)
      an [IndexOutOfBoundsException] should be thrownBy {
        mgr.nextCard()
      }
      mgr.currentIndx shouldBe 51
    }
  }
}

