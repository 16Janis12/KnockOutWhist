package de.knockoutwhist.logic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.control.controllerBaseImpl.MatchLogic
import de.knockoutwhist.player.PlayerFactory
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.rounds.{Match, Round}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class MatchLogicTests extends AnyWordSpec with Matchers {

  "The Match logic" should {
    "check the already finished rounds and return false when it is empty (because the Match can't be finished" in {
      val player1 = PlayerFactory.createPlayer("Gunter", UUID.randomUUID(), HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", UUID.randomUUID(), HUMAN)
      val match1 = Match(List(player1, player2), 5, true, List(), KnockOutWhist.config.cardManager)
      MatchLogic.isOver(match1) shouldBe false
    }
    "return true when calling isOver if the match is over" in {
      val player1 = PlayerFactory.createPlayer("Gunter", UUID.randomUUID(), HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", UUID.randomUUID(), HUMAN)
      val round1 = new Round(Suit.Spades, List(player1), false)
      val match1 = Match(List(player1, player2), 5, true, List(round1), KnockOutWhist.config.cardManager)
      MatchLogic.isOver(match1) shouldBe true
    }
  }
}
