package de.knockoutwhist.rounds

import de.knockoutwhist.cards.{Card, CardManager, CardValue, Suit}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

import scala.List
import scala.collection.mutable.ListBuffer


class MatchTests extends AnyWordSpec with Matchers{

//  "A Match" should {
//    TestUtil.disableDelay()
//    TestUtil.cancelOut() {
//      TestUtil.disableDebugMode()
//      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
//      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
//      val player_list = List(player1, player2)
//      val match1 = Match(player_list)
//      val round1 = RoundControl.createround(match1)
//      val trumpsuit = round1.trumpSuit
//      var trick1 = TrickControl.createtrick(round1)
//      val playedcard1 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player1, trick1)
//      }
//      trick1 = TrickControl.playCard(trick1, round1, playedcard1, player1)._1
//      val playedcard2 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player2, trick1)
//      }
//      trick1 = TrickControl.playCard(trick1, round1, playedcard2, player2)._1
//      "return the players ingame in players_remaining" in {
//        RoundControl.remainingPlayers(round1) should be(player_list)
//      }
//      val rtrick1 = TrickControl.wonTrick(trick1, round1)
//      RoundControl.finalizeRound(round1, match1, true)
//      val round2 = TestUtil.simulateInput("1\n") {
//        RoundControl.createround(match1)
//      }
//      TestUtil.enableDebugMode()
//      var trick2: Trick = TrickControl.createtrick(round2)
//      val playedcard3 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player1, trick2)
//      }
//      trick1 = TrickControl.playCard(trick1, round2, playedcard3, player1)._1
//      val playedcard4 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player2, trick2)
//      }
//      trick2 = TrickControl.playCard(trick2, round2, playedcard4, player2)._1
//      "be able to return the current trick of the round" in {
//        round2.currenttrick.get should be(trick2)
//      }
//      val rtrick2 = TrickControl.wonTrick(trick2, round2)
//      "return false if the round isn't over" in {
//        RoundControl.isOver(round2) shouldBe false
//      }
//      "be able to tell if a dog needs to play" in {
//        RoundControl.dogNeedsToPlay(round2) shouldBe false
//      }
//      "error out if a round is finalized without all tricks played" in {
//        assertThrows[IllegalStateException] {
//          RoundControl.finalizeRound(round2, match1)
//        }
//      }
//      RoundControl.finalizeRound(round2, match1, true)
//      val round3 = TestUtil.simulateInput("1\n") {
//        RoundControl.createround(match1)
//      }
//      var trick3 = TrickControl.createtrick(round3)
//      val playedcard5 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player1, trick3)
//      }
//      trick3 = TrickControl.playCard(trick3, round3, playedcard5, player1)._1 //stand trick1
//      val playedcard6 = TestUtil.simulateInput("1\n") {
//        PlayerControl.playCard(player2, trick3)
//      }
//      trick3 = TrickControl.playCard(trick3, round3, playedcard6, player2)._1
//      TrickControl.wonTrick(trick3, round3)
//      "throw an exception if a match gets finalized before it is finished" in {
//        assertThrows[IllegalStateException] { //If exception is thrown, assertThrows returns succeeded
//          MatchControl.finalizeMatch(match1)
//        }
//      }
//      "be able to create a random trumpsuit for first round" in {
//        round2.trumpSuit shouldBe Suit.Hearts
//      }
//      "return false when no round has been completed" in {
//        CardManager.shuffleAndReset()
//        val match3 = Match(List(PlayerFactory.createPlayer("Gunter", HUMAN)))
//        RoundControl.createround(match3)
//        MatchControl.isOver(match3) shouldBe false
//      }
//      "return true if one player is remaining after a round has been played" in {
//        RoundControl.finalizeRound(round3, match1, true)
//        MatchControl.isOver(match1) shouldBe true
//      }
//      val round4 = TestUtil.simulateInput("1\n") {
//        RoundControl.createround(match1)
//      }
//      var trick4 = TrickControl.createtrick(round4)
//      val playedcard7 = Card(CardValue.Ace, Suit.Hearts)
//      val playedcard8 = Card(CardValue.Two, Suit.Hearts)
//      trick4 = TrickControl.playCard(trick4, round4, playedcard7, player1)._1
//      trick4 = TrickControl.playCard(trick4, round4, playedcard8, player2)._1
//      TrickControl.wonTrick(trick4, round4)
//
//      var trick5 = TrickControl.createtrick(round4)
//      trick5 = TrickControl.playCard(trick5, round4, playedcard8, player1)._1
//      trick5 = TrickControl.playCard(trick5, round4, playedcard7, player2)._1
//      TrickControl.wonTrick(trick5, round4)
//      CardManager.shuffleAndReset()
//      val roundResult = TestUtil.simulateInput("1\n13\n") {
//        RoundControl.finalizeRound(round4, match1, true)
//      }
//
//      val round5 = TestUtil.simulateInput("1\n") {
//        RoundControl.createround(match1)
//      }
//      "error out if a round is finalized without any tricks played" in {
//        assertThrows[IllegalStateException] {
//          RoundControl.finalizeRound(round5, match1)
//        }
//      }
//      "be able to finalize a round" in {
//        roundResult._1 should be(player1).or(be(player2))
//      }
//
//      "provide a toString for the rounds" in {
//        round5.toString should be(s"${Suit.Hearts}, ${ListBuffer()}, $player_list, null, null, false")
//      }
//      "show the winner of the match when it has ended" in {
//        MatchControl.finalizeMatch(match1) shouldBe PlayerFactory.createPlayer("Peter", HUMAN)
//      }
//      "have a working toString Method" in {
//        match1.toString shouldBe "List(Gunter, Peter), 2"
//      }
//    }
//  }
}
