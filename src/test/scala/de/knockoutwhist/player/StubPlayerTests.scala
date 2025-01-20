package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.TrickLogic
import de.knockoutwhist.player.Playertype.HUMAN
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.player.StubPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

class StubPlayerTests extends AnyWordSpec with Matchers {
  "The StubPlayer" should {
    "be able to provide a hand" in {
      val hand1 = Hand(List(Card(CardValue.Two, Suit.Hearts), Card(CardValue.Three, Suit.Hearts)))
      val player1 = StubPlayer("Gunter", Some(hand1))
      val player2 = StubPlayer("Peter", Some(hand1))
      player1.provideHand(hand1) shouldBe player1
    }
    "be able to remove a card" in {
      val hand1 = Hand(List(Card(CardValue.Two, Suit.Hearts), Card(CardValue.Three, Suit.Hearts)))
      val player1 = StubPlayer("Gunter", Some(hand1))
      val player2 = StubPlayer("Peter", Some(hand1))
      player1.removeCard(Card(CardValue.Two, Suit.Hearts)).name shouldBe player1.name
    }
    "be able to set the doglife" in {
      val hand1 = Hand(List(Card(CardValue.Two, Suit.Hearts), Card(CardValue.Three, Suit.Hearts)))
      val player1 = StubPlayer("Gunter", Some(hand1))
      player1.setDogLife() shouldBe StubPlayer("Gunter", Some(hand1), true)
    }
    "be able to handle a Card played" in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      val round1 = Round(Suit.Spades, List(), List(player1, player2), List(), 0, player1, false)
      val trick1 = Trick()
      val match1 = Match(List(player1, player2), 5, false, List(round1))
      val hand1 = Hand(List(Card(CardValue.Two, Suit.Hearts), Card(CardValue.Three, Suit.Hearts)))
      val hand2 = Hand(List(Card(CardValue.Four, Suit.Hearts), Card(CardValue.Five, Suit.Hearts)))
      player1.provideHand(hand1)
      player2.provideHand(hand2)
      ControlThread.runLater {
        player1.handlePlayCard(hand1, match1, round1, trick1, 0)
      }
    }
  }


}
