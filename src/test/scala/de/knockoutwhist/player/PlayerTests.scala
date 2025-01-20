package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerTests extends AnyWordSpec with Matchers {

//  "A player" should {
//    TestUtil.disableDelay()
//    "be able to have a hand" in {
//      val card = Card(CardValue.Ace, Suit.Spades)
//      val card2 = Card(CardValue.Ten, Suit.Spades)
//      val card3 = Card(CardValue.Ten, Suit.Diamonds)
//      val listCard = List(card, card2, card3)
//      val testhand = Hand(listCard)
//      val player = PlayerFactory.createPlayer("Gunter", HUMAN)
//      player.provideHand(testhand) shouldBe true
//    }
//    "be able to remove a Card" in {
//      val card = Card(CardValue.Ace, Suit.Spades)
//      val card2 = Card(CardValue.Ten, Suit.Spades)
//      val card3 = Card(CardValue.Ten, Suit.Diamonds)
//      val listCard = List(card, card2, card3)
//      val testhand = Hand(listCard)
//      val player = PlayerFactory.createPlayer("Gunter", HUMAN)
//      player.provideHand(testhand)
//      player.removeCard(card) shouldBe 2
//      player.currentHand().get.cards should be (List(card2, card3))
//    }
//  }

}
