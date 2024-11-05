package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.rounds.{Match, Round, Trick}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.cards.{Player, Suit}
import de.knockoutwhist.testutils.TestUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class RoundTests extends AnyWordSpec with Matchers{
  "A Match" should {
    val player1 = Player("Gunter")
    val player2 = Player("Peter")
    val player_list = List(player1, player2)
    val match1 = Match(player_list)
    val round1 = match1.create_round()
    val trumpsuit = round1.trumpSuit
    val trick1 = round1.create_trick()
    val playedcard1 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player1)
    }
    trick1.playCard(playedcard1, player1)
    val playedcard2 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player2)
    }
    trick1.playCard(playedcard2, player2)
    trick1.wonTrick()
    round1.finalizeRound(true)
    val round2 = TestUtil.simulateInput("1\n") {
      match1.create_round()
    }
    val trick2 = round2.create_trick()
    val playedcard3 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player1)
    }
    trick1.playCard(playedcard3, player1)
    val playedcard4 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player2)
    }
    trick2.playCard(playedcard4, player2)
    trick2.wonTrick()
    round2.finalizeRound(true)
    val round3 = TestUtil.simulateInput("1\n") {
      match1.create_round()
    }
    val trick3 = round3.create_trick()
    val playedcard5 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player1)
    }
    trick1.playCard(playedcard5, player1)
    val playedcard6 = TestUtil.simulateInput("1\n") {
      KnockOutWhist.matchControl.playerControl.playCard(player2)
    }
    trick3.playCard(playedcard6, player2)
    trick3.wonTrick()
    "throw an exception if a match gets finalized before it is finished" in {
      assertThrows[IllegalStateException] { //If exception is thrown, assertThrows returns succeeded
        match1.finalizeMatch()
      }
    }
    "be able to create a random trumpsuit for first round" in {
      round2.trumpSuit shouldBe Suit.Hearts


    }
    "return false when no round has been completed" in {
        val match3 = Match(List(Player("Gunter")))
        match3.create_round()
        match3.isOver shouldBe false
      }
    "return true if one player is remaining after a round has been played" in {
      round3.finalizeRound(true)
      match1.isOver shouldBe true
    }
    "show the winner of the match when it has ended" in {
      //round3.finalizeRound(true)
      match1.finalizeMatch() shouldBe Player("Peter")
    }
    "have a working toString Method" in {
      match1.toString shouldBe "List(Gunter, Peter), 4"
    }
    

  }
}
