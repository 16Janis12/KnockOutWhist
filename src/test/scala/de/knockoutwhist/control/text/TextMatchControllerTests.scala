package de.knockoutwhist.control.text

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.CardValue.Ace
import de.knockoutwhist.cards.Suit.Hearts
import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.utils.CustomPlayerQueue
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

class TextMatchControllerTests extends AnyWordSpec with Matchers {
  "The start function" should {
    "throw no exception" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("a\n2\n") {
          TextMatchControl.start()
        }
      }
    }
  }

  "The enter players function" should {
    "throw no exception" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("foo,bar\n") {
          TextMatchControl.enterPlayers() should be (List(Player("foo"), Player("bar")))
        }
      }
    }
    "not accept less than 2 players" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("foo\nbar,foo2\n") {
          TextMatchControl.enterPlayers() should be (List(Player("bar"), Player("foo2")))
        }
      }
    }
    "not accept players with the same name" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("foo,foo\nbar,foo\n") {
          TextMatchControl.enterPlayers() should be (List(Player("bar"), Player("foo")))
        }
      }
    }
    "not accept player names less than 2 or greater than 10 characters" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("f,b\nbarrrrrrrrrrrrrrrrr,foooooooooooooooooooo\nbar,foo\n") {
          TextMatchControl.enterPlayers() should be (List(Player("bar"), Player("foo")))
        }
      }
    }
  }

  "The control round function" should {
    "throw no exception and return a winner" in {
      val players = List(Player("foo"), Player("bar"))
      val matchImpl = Match(players, 1)
      TestUtil.disableDebugMode()
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("1\n1\n1\n") {
          TextMatchControl.controlRound(matchImpl).winner should be (players.head).or(be (players(1)))
        }
      }
    }
  }



  "The next round function" should {
    "return null if the match is over" in {
      val players = List(Player("foo"))
      val matchImpl = Match(players, 2)
      TestUtil.enableDebugMode()
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("1\n1\n1\n") {
          TextMatchControl.controlRound(matchImpl)
          TextMatchControl.nextRound(matchImpl) should be (null)
        }
      }
    }
  }

  "The next trick function" should {
    "return null if the round is over" in {
      val players = List(Player("foo"))
      val matchImpl = Match(players, 2)
      TestUtil.enableDebugMode()
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("1\n1\n1\n") {
          val round = TextMatchControl.controlRound(matchImpl)
          TextMatchControl.nextTrick(round) should be (null)
        }
      }
    }
  }
  "The controlSuit function" should {
    "check if a player can play from the correct suit but doesnt" in {
      val player1 = Player("Gunter")
      val player2 = Player("Peter")
      val players = List(player1, player2)
      val hand =  Hand(List(Card(CardValue.Ten, Suit.Spades),Card(CardValue.Two, Suit.Hearts)))
      player1.provideHand(hand)
      val matchImpl = Match(players, 2)
      val round = new Round(Suit.Clubs, matchImpl, players, false)
      val trick = new Trick(round)
      trick.playCard(Card(Ace, Suit.Hearts), player2)
      TestUtil.enableDebugMode()
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("1\n2\n") {
          val card = TextMatchControl.controlSuitplayed(trick, player1)
        }
      }
    }
  }

  "The control Trick function" should {
    "return the other player if the dog decides not to play" in {
      val foo = Player("foo")
      foo.doglife = true
      foo.provideHand(CardManager.createHand(1))
      val bar = Player("bar")
      bar.provideHand(CardManager.createHand(3))
      val players = List(foo, bar)
      val matchImpl = Match(players, 2)
      TestUtil.enableDebugMode()
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      val round = new Round(Hearts,matchImpl,players,false)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("n\n1\n") {
          val finalTrick = TextMatchControl.controlTrick(round)
          finalTrick.winner should be(bar)
        }
      }
    }
    "return the dog if he wins" in {
      TestUtil.enableDebugMode()
      CardManager.resetOrder()
      for (i <- 0 to 12) {
        CardManager.nextCard()
      }
      val foo = Player("foo")
      foo.doglife = true
      foo.provideHand(CardManager.createHand(1))
      val bar = Player("bar")
      bar.provideHand(CardManager.createHand(3))
      val players = List(foo, bar)
      val matchImpl = Match(players, 2)
      TextMatchControl.playerQueue = CustomPlayerQueue[Player](players.toArray[Player], 0)
      val round = new Round(foo.currentHand().get.cards.head.suit, matchImpl, players, false)
      TestUtil.cancelOut() {
        TestUtil.simulateInput("y\n1\n") {
          val finalTrick = TextMatchControl.controlTrick(round)
          finalTrick.winner should be(bar)
        }
      }
    }
  }


}
