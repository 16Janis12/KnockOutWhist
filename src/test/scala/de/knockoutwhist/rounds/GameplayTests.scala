package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, CardValue, Suit}
import de.knockoutwhist.player.Player
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

import scala.List
import scala.collection.mutable.ListBuffer


class GameplayTests extends AnyWordSpec with Matchers{

  "A Match" should {
    TestUtil.cancelOut() {
      TestUtil.disableDebugMode()
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
      "return the players ingame in players_remaining" in {
        round1.remainingPlayers() should be(player_list)
      }
      val rtrick1 = trick1.wonTrick()
      round1.finalizeRound(true)
      val round2 = TestUtil.simulateInput("1\n") {
        match1.create_round()
      }
      TestUtil.enableDebugMode()
      var trick2: Trick = null
      "should be able to create a trick with requesting the current trick" in {
        trick2 = round2.get_current_trick()
      }
      if (trick2 == null) {
        trick2 = round2.get_current_trick()
      }
      val playedcard3 = TestUtil.simulateInput("1\n") {
        KnockOutWhist.matchControl.playerControl.playCard(player1)
      }
      trick1.playCard(playedcard3, player1)
      val playedcard4 = TestUtil.simulateInput("1\n") {
        KnockOutWhist.matchControl.playerControl.playCard(player2)
      }
      trick2.playCard(playedcard4, player2)
      "be able to return the current trick of the round" in {
        round2.get_current_trick() should be(trick2)
      }
      val rtrick2 = trick2.wonTrick()
      "return false if the round isn't over" in {
        round2.isOver shouldBe false
      }
      "be able to return all the tricks of the round" in {
        round1.get_tricks() should be(List(rtrick1._2))
      }
      "be able to tell if a dog needs to play" in {
        round2.dogNeedsToPlay shouldBe false
      }
      "error out if a round is finalized without all tricks played" in {
        assertThrows[IllegalStateException] {
          round2.finalizeRound()
        }
      }
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
        CardManager.shuffleAndReset()
        val match3 = Match(List(Player("Gunter")))
        match3.create_round()
        match3.isOver shouldBe false
      }
      "return true if one player is remaining after a round has been played" in {
        round3.finalizeRound(true)
        match1.isOver shouldBe true
      }
      val round4 = TestUtil.simulateInput("1\n") {
        match1.create_round()
      }
      val trick4 = round4.create_trick()
      val playedcard7 = Card(CardValue.Ace, Suit.Hearts)
      val playedcard8 = Card(CardValue.Two, Suit.Hearts)
      trick4.playCard(playedcard7, player1)
      trick4.playCard(playedcard8, player2)
      trick4.wonTrick()

      val trick5 = round4.create_trick()
      trick5.playCard(playedcard8, player1)
      trick5.playCard(playedcard7, player2)
      trick5.wonTrick()
      CardManager.shuffleAndReset()
      val roundResult = TestUtil.simulateInput("1\n13\n") {
        round4.finalizeRound(true)
      }

      val round5 = TestUtil.simulateInput("1\n") {
        match1.create_round()
      }
      "error out if a round is finalized without any tricks played" in {
        assertThrows[IllegalStateException] {
          round5.finalizeRound()
        }
      }
      "be able to finalize a round" in {
        roundResult._1 should be(player1).or(be(player2))
      }

      "provide a toString for the rounds" in {
        round5.toString should be(s"${Suit.Hearts}, ${ListBuffer()}, $player_list, null, null, false")
      }
      "show the winner of the match when it has ended" in {
        match1.finalizeMatch() shouldBe Player("Peter")
      }
      "have a working toString Method" in {
        match1.toString shouldBe "List(Gunter, Peter), 2"
      }
    }
  }
}
