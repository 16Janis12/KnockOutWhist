package de.knockoutwhist.player

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.player.baseImpl.HumanPlayer
import de.knockoutwhist.cards.{Card, CardValue, Suit, Hand}

class HumanPlayerSpec extends AnyWordSpec with Matchers {

  "HumanPlayer" should {
    "behave like an AbstractPlayer for hand/dog life operations" in {
      val p: AbstractPlayer = new HumanPlayer("alice")
      p.name shouldBe "alice"
      p.isInDogLife shouldBe false
      p.setDogLife()
      p.isInDogLife shouldBe true
      p.resetDogLife()
      p.isInDogLife shouldBe false

      val c = Card(CardValue.Ace, Suit.Spades)
      p.currentHand() shouldBe None
      p.provideHand(Hand(List(c)))
      p.currentHand().isDefined shouldBe true
      p.removeCard(c)
      p.currentHand().get.cards should not contain c
    }
  }
}
