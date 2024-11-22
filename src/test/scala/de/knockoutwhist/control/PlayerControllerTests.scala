package de.knockoutwhist.control

import de.knockoutwhist.cards.Suit.*
import de.knockoutwhist.cards.{CardManager, Hand}
import de.knockoutwhist.control.PlayerControl
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PlayerControllerTests extends AnyWordSpec with Matchers {

  "The text player controller play function" should {
    TestUtil.disableDelay()
    CardManager.shuffleAndReset()
    TestUtil.cancelOut() {
      "throw an exception of the player has no hand" in {
        assertThrows[NoSuchElementException] {
          TestUtil.simulateInput("1\n") {
            PlayerControl.playCard(Player("Foo"))
          }
        }
      }
      "ask again on an invalid input" in {
        val player = Player("Foo")
        CardManager.shuffleAndReset()
        val hand = CardManager.createHand(1)
        player.provideHand(hand)
        val card = TestUtil.simulateInput("0\na\n1\n") {
          PlayerControl.playCard(player)
        }
        card should be(hand.cards.head)
      }
      "provide the card the player selected" in {
        val player = Player("Foo")
        CardManager.shuffleAndReset()
        val hand = CardManager.createHand(1)
        player.provideHand(hand)
        val card = TestUtil.simulateInput("1\n") {
          PlayerControl.playCard(player)
        }
        card should be(hand.cards.head)
      }
    }
  }

  "The text player controller dogplay function" should {
    val player = Player("Bar")
    CardManager.shuffleAndReset()
    val hand = CardManager.createHand(2)
    player.provideHand(hand)
    val round = new Round(Spades, null, List(player), false)
    TestUtil.cancelOut() {
      "throw an exception of the player has no hand" in {
        assertThrows[NoSuchElementException] {
          TestUtil.simulateInput("y\n") {
            TestUtil.disableDebugMode()
            PlayerControl.dogplayCard(Player("Foo"), round)
          }
        }
      }
      "ask again on an invalid input" in {
        TestUtil.enableDebugMode()
        val player = Player("Foo")
        CardManager.shuffleAndReset()
        val hand = CardManager.createHand(1)
        player.provideHand(hand)
        val card = TestUtil.simulateInput("a\ny\n") {
          PlayerControl.dogplayCard(player, round)
        }
        card should be(Some(hand.cards.head))
      }
      "provide the card the player selected" in {
        TestUtil.enableDebugMode()
        val player = Player("Foo")
        CardManager.shuffleAndReset()
        val hand = CardManager.createHand(1)
        player.provideHand(hand)
        val card = TestUtil.simulateInput("y\n") {
          PlayerControl.dogplayCard(player, round)
        }
        card should be(Some(hand.cards.head))
      }
      "allow the dog to not play this trick" in {
        TestUtil.enableDebugMode()
        val player = Player("Foo")
        CardManager.shuffleAndReset()
        val hand = CardManager.createHand(1)
        player.provideHand(hand)
        val card = TestUtil.simulateInput("n\n") {
          PlayerControl.dogplayCard(player, round)
        }
        card should be(None)
      }
      "force the dog to play in the last round" in {
        TestUtil.enableDebugMode()
        val player1 = Player("Foo")
        CardManager.shuffleAndReset()
        player1.provideHand(CardManager.createHand(1))
        player1.doglife = true
        val player2 = Player("Bar")
        player2.provideHand(Hand(List()))
        val round2 = new Round(Spades, null, List(player1,player2), true)
        val card = TestUtil.simulateInput("n\ny\n") {
          PlayerControl.dogplayCard(player1, round2)
        }
        card should be(Some(player1.currentHand().get.cards.head))
      }
    }
  }

  "The text player controller determineWinnerTie function" should {
    TestUtil.cancelOut() {
      "return the player with the highest card" in {
        TestUtil.disableDebugMode()
        val player1 = Player("Foo")
        val player2 = Player("Bar")
        val players = List(player1, player2)
        val winner = TestUtil.simulateInput("1\n2\n") {
          PlayerControl.determineWinnerTie(players)
        }
        winner should be(player2).or(be(player1))
      }
      "return the player with the highest card after a tie" in {
        TestUtil.enableDebugMode()
        CardManager.resetOrder()
        val player1 = Player("Foo")
        val player2 = Player("Bar")
        val players = List(player1, player2)
        val winner = TestUtil.simulateInput("1\n13\n5\n1\n") {
          PlayerControl.determineWinnerTie(players)
        }
        winner should be(player2)
      }
      "return the player with the highest card after a tie (winner first)" in {
        TestUtil.enableDebugMode()
        CardManager.resetOrder()
        val player1 = Player("Foo")
        val player2 = Player("Bar")
        val players = List(player1, player2)
        val winner = TestUtil.simulateInput("13\n1\n") {
          PlayerControl.determineWinnerTie(players)
        }
        winner should be(player1)
      }
      "ask again on an invalid input" in {
        TestUtil.enableDebugMode()
        CardManager.resetOrder()
        val player1 = Player("Foo")
        val player2 = Player("Bar")
        val players = List(player1, player2)
        val winner = TestUtil.simulateInput("a\n200\n1\n2\n") {
          PlayerControl.determineWinnerTie(players)
        }
        winner should be(player2)
      }
    }
  }

  "The text player controller pickNextTrumpsuit function" should {
    TestUtil.cancelOut() {
      "return the suit the player selected (Spades)" in {
        TestUtil.disableDebugMode()
        val player = Player("Foo")
        player.provideHand(CardManager.createHand(4))
        val suit = TestUtil.simulateInput("4\n") {
          PlayerControl.pickNextTrumpsuit(player)
        }
        suit should be(Spades)
      }
      "return the suit the player selected (Hearts)" in {
        TestUtil.disableDebugMode()
        val player = Player("Foo")
        player.provideHand(CardManager.createHand(4))
        val suit = TestUtil.simulateInput("1\n") {
          PlayerControl.pickNextTrumpsuit(player)
        }
        suit should be(Hearts)
      }
      "return the suit the player selected (Diamonds)" in {
        TestUtil.disableDebugMode()
        val player = Player("Foo")
        player.provideHand(CardManager.createHand(4))
        val suit = TestUtil.simulateInput("2\n") {
          PlayerControl.pickNextTrumpsuit(player)
        }
        suit should be(Diamonds)
      }
      "return the suit the player selected (Clubs)" in {
        TestUtil.disableDebugMode()
        val player = Player("Foo")
        player.provideHand(CardManager.createHand(4))
        val suit = TestUtil.simulateInput("3\n") {
          PlayerControl.pickNextTrumpsuit(player)
        }
        suit should be(Clubs)
      }
      "ask again on an invalid input" in {
        TestUtil.enableDebugMode()
        val player = Player("Foo")
        player.provideHand(CardManager.createHand(4))
        val suit = TestUtil.simulateInput("a\n10\n1\n") {
          PlayerControl.pickNextTrumpsuit(player)
        }
        suit should be(Hearts)
      }
    }
  }

//  "The text player controller showCards function" should {
//    TestUtil.cancelOut() {
//      "return true if the player wants to show their cards" in {
//        TestUtil.disableDebugMode()
//        val player = Player("Foo")
//        player.provideHand(CardManager.createHand(4))
//        TextPlayerControl.showCards(player) should be(true)
//      }
//      "return false if the player does not want to show their cards" in {
//        TestUtil.disableDebugMode()
//        val player = Player("Foo")
//        TextPlayerControl.showCards(player) should be(false)
//      }
//    }
//  }
//
//  "The text player controller showWon function" should {
//    TestUtil.cancelOut() {
//      "print the winner of the match" in {
//        TestUtil.disableDebugMode()
//        val player = Player("Foo")
//        TextPlayerControl.showWon(player, null) should be("Foo won this round.")
//      }
//    }
//  }

}
