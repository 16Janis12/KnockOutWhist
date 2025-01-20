package de.knockoutwhist.controlold

import de.knockoutwhist.cards.Suit.*
import de.knockoutwhist.cards.{Card, CardManager, Hand}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerControllerTests extends AnyWordSpec with Matchers {

//  "The text player controller play function" should {
////    TestUtil.disableDelay()
////    CardManager.shuffleAndReset()
////    val match1 = Match(List(PlayerFactory.createPlayer("Gunter", HUMAN)))
////    val roundtest = RoundControl.createround(match1)
////    val trick = TrickControl.createtrick(roundtest)
////    TestUtil.cancelOut() {
////      "throw an exception of the player has no hand" in {
////        assertThrows[NoSuchElementException] {
////          TestUtil.simulateInput("1\n") {
////            PlayerControl.playCard(PlayerFactory.createPlayer("Foo", HUMAN), trick)
////          }
////        }
////      }
////      "ask again on an invalid input" in {
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        val hand = CardManager.createHand(1)
////        player.provideHand(hand)
////        val card = TestUtil.simulateInput("0\na\n1\n") {
////          PlayerControl.playCard(player, trick)
////        }
////        card should be(hand.cards.head)
////      }
////      "provide the card the player selected" in {
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        val hand = CardManager.createHand(1)
////        player.provideHand(hand)
////        val card = TestUtil.simulateInput("1\n") {
////          PlayerControl.playCard(player, trick)
////        }
////        card should be(hand.cards.head)
////      }
////    }
//  }
//
////  "The text player controller dogplay function" should {
////    val player = PlayerFactory.createPlayer("Bar", HUMAN)
////    CardManager.shuffleAndReset()
////    val hand = CardManager.createHand(2)
////    player.provideHand(hand)
////    val matchimpl = Match(List(PlayerFactory.createPlayer("Gunter", HUMAN)))
////    val round = new Round(Spades, matchimpl, List(player), false)
////    val tricktest = TrickControl.createtrick(round)
////    TestUtil.cancelOut() {
////      "throw an exception of the player has no hand" in {
////        assertThrows[NoSuchElementException] {
////          TestUtil.simulateInput("y\n") {
////            TestUtil.disableDebugMode()
////            PlayerControl.dogplayCard(PlayerFactory.createPlayer("Foo", HUMAN), round, tricktest)
////          }
////        }
////      }
////      "ask again on an invalid input" in {
////        TestUtil.enableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        val hand = CardManager.createHand(1)
////        player.provideHand(hand)
////        val card = TestUtil.simulateInput("a\ny\n") {
////          PlayerControl.dogplayCard(player, round, tricktest)
////        }
////        card should be(Some(hand.cards.head))
////      }
////      "provide the card the player selected" in {
////        TestUtil.enableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        val hand = CardManager.createHand(1)
////        player.provideHand(hand)
////        val card = TestUtil.simulateInput("y\n") {
////          PlayerControl.dogplayCard(player, round, tricktest)
////        }
////        card should be(Some(hand.cards.head))
////      }
////      "allow the dog to not play this trick" in {
////        TestUtil.enableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        val hand = CardManager.createHand(1)
////        player.provideHand(hand)
////        val card = TestUtil.simulateInput("n\n") {
////          PlayerControl.dogplayCard(player, round, tricktest)
////        }
////        card should be(None)
////      }
////      "force the dog to play in the last round" in {
////        TestUtil.enableDebugMode()
////        val player1 = PlayerFactory.createPlayer("Foo", HUMAN)
////        CardManager.shuffleAndReset()
////        player1.provideHand(CardManager.createHand(1))
////        player1.doglife = true
////        val player2 = PlayerFactory.createPlayer("Bar", HUMAN)
////        player2.provideHand(Hand(List()))
////        val round2 = new Round(Spades, matchimpl, List(player1,player2), true)
////        val card = TestUtil.simulateInput("n\ny\n") {
////          PlayerControl.dogplayCard(player1, round2, tricktest)
////        }
////        card should be(Some(player1.currentHand().get.cards.head))
////      }
////    }
////  }
//
//  "The text player controller determineWinnerTie function" should {
//    TestUtil.cancelOut() {
////      "return the player with the highest card" in {
////        TestUtil.disableDebugMode()
////        val player1 = PlayerFactory.createPlayer("Foo", HUMAN)
////        val player2 = PlayerFactory.createPlayer("Bar", HUMAN)
////        val players = List(player1, player2)
////        val winner = TestUtil.simulateInput("1\n2\n") {
////          PlayerControl.determineWinnerTie(players)
////        }
////        winner should be(player2).or(be(player1))
////      }
////      "return the player with the highest card after a tie" in {
////        TestUtil.enableDebugMode()
////        CardManager.resetOrder()
////        val player1 = PlayerFactory.createPlayer("Foo", HUMAN)
////        val player2 = PlayerFactory.createPlayer("Bar", HUMAN)
////        val players = List(player1, player2)
////        val winner = TestUtil.simulateInput("1\n13\n5\n1\n") {
////          PlayerControl.determineWinnerTie(players)
////        }
////        winner should be(player1)
////      }
////      "return the player with the highest card after a tie (winner first)" in {
////        TestUtil.enableDebugMode()
////        CardManager.resetOrder()
////        val player1 = PlayerFactory.createPlayer("Foo", HUMAN)
////        val player2 = PlayerFactory.createPlayer("Bar", HUMAN)
////        val players = List(player1, player2)
////        val winner = TestUtil.simulateInput("13\n1\n") {
////          PlayerControl.determineWinnerTie(players)
////        }
////        winner should be(player1)
////      }
////      "ask again on an invalid input" in {
////        TestUtil.enableDebugMode()
////        CardManager.resetOrder()
////        val player1 = PlayerFactory.createPlayer("Foo", HUMAN)
////        val player2 = PlayerFactory.createPlayer("Bar", HUMAN)
////        val players = List(player1, player2)
////        val winner = TestUtil.simulateInput("a\n200\n1\n2\n") {
////          PlayerControl.determineWinnerTie(players)
////        }
////        winner should be(player2)
////      }
////    }
//  }
//
//  "The text player controller pickNextTrumpsuit function" should {
//    TestUtil.cancelOut() {
////      "return the suit the player selected (Spades)" in {
////        TestUtil.disableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        player.provideHand(CardManager.createHand(4))
////        val suit = TestUtil.simulateInput("4\n") {
////          PlayerControl.pickNextTrumpsuit(player)
////        }
////        suit should be(Spades)
////      }
////      "return the suit the player selected (Hearts)" in {
////        TestUtil.disableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        player.provideHand(CardManager.createHand(4))
////        val suit = TestUtil.simulateInput("1\n") {
////          PlayerControl.pickNextTrumpsuit(player)
////        }
////        suit should be(Hearts)
////      }
////      "return the suit the player selected (Diamonds)" in {
////        TestUtil.disableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        player.provideHand(CardManager.createHand(4))
////        val suit = TestUtil.simulateInput("2\n") {
////          PlayerControl.pickNextTrumpsuit(player)
////        }
////        suit should be(Diamonds)
////      }
////      "return the suit the player selected (Clubs)" in {
////        TestUtil.disableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        player.provideHand(CardManager.createHand(4))
////        val suit = TestUtil.simulateInput("3\n") {
////          PlayerControl.pickNextTrumpsuit(player)
////        }
////        suit should be(Clubs)
////      }
////      "ask again on an invalid input" in {
////        TestUtil.enableDebugMode()
////        val player = PlayerFactory.createPlayer("Foo", HUMAN)
////        player.provideHand(CardManager.createHand(4))
////        val suit = TestUtil.simulateInput("a\n10\n1\n") {
////          PlayerControl.pickNextTrumpsuit(player)
////        }
////        suit should be(Hearts)
////      }
////    }
//  }
//
////  "The text player controller showCards function" should {
////    TestUtil.cancelOut() {
////      "return true if the player wants to show their cards" in {
////        TestUtil.disableDebugMode()
////        val player = Player("Foo")
////        player.provideHand(CardManager.createHand(4))
////        TextPlayerControl.showCards(player) should be(true)
////      }
////      "return false if the player does not want to show their cards" in {
////        TestUtil.disableDebugMode()
////        val player = Player("Foo")
////        TextPlayerControl.showCards(player) should be(false)
////      }
////    }
////  }
////
////  "The text player controller showWon function" should {
////    TestUtil.cancelOut() {
////      "print the winner of the match" in {
////        TestUtil.disableDebugMode()
////        val player = Player("Foo")
////        TextPlayerControl.showWon(player, null) should be("Foo won this round.")
////      }
////    }
////  }
//
}
