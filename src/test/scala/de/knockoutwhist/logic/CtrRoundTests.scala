package de.knockoutwhist.logic

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.MainLogic
import de.knockoutwhist.player.PlayerFactory
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec


class CtrRoundTests extends AnyWordSpec with Matchers {
  "Control Round" should {
    TestUtil.disableDelay()
    "Work" in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      val hand1 = Hand(List(Card(CardValue.Two, Suit.Hearts), Card(CardValue.Three, Suit.Hearts)))
      val hand2 = Hand(List(Card(CardValue.Four, Suit.Hearts), Card(CardValue.Five, Suit.Hearts)))
      player1.provideHand(hand1)
      player2.provideHand(hand2)
      val round1 = Round(Suit.Spades, List(), List(player1, player2), List(), 0, player1, false)
      val match1 = Match(List(player1, player2), 2, true, List(round1))
      ControlThread.runLater {
        TestUtil.simulateInput("2\n") {
          MainLogic.controlRound(match1, round1)
          System.exit(0)
        }
      }
    }
  }


}
