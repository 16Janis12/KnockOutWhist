package de.knockoutwhist.controlold

import de.knockoutwhist.cards.CardValue.Ace
import de.knockoutwhist.cards.Suit.Hearts
import de.knockoutwhist.cards.*
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.MainLogic
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.utils.CustomPlayerQueue
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class MatchControllerTests extends AnyWordSpec with Matchers {

  "The enter players function" should {
    TestUtil.disableDelay()
        "throw no exception" in {
          TestUtil.cancelOut() {
            TestUtil.simulateInput("foo,bar\n") {
              ControlThread.runLater {
                MainLogic.startMatch() should not be(List(PlayerFactory.createPlayer("foo", UUID.randomUUID(), HUMAN), PlayerFactory.createPlayer("bar", UUID.randomUUID(), HUMAN)))
              }
            }
          }
        }
    //    "not accept less than 2 players" in {
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("foo\nbar,foo2\n") {
    //          MatchControl.enterPlayers() should be (List(PlayerFactory.createPlayer("bar", HUMAN), PlayerFactory.createPlayer("foo2", HUMAN)))
    //        }
    //      }
    //    }
    //    "not accept players with the same name" in {
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("foo,foo\nbar,foo\n") {
    //          MatchControl.enterPlayers() should be (List(PlayerFactory.createPlayer("bar", HUMAN), PlayerFactory.createPlayer("foo", HUMAN)))
    //        }
    //      }
    //    }
    //    "not accept player names less than 2 or greater than 10 characters" in {
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("f,b\nbarrrrrrrrrrrrrrrrr,foooooooooooooooooooo\nbar,foo\n") {
    //          MatchControl.enterPlayers() should be (List(PlayerFactory.createPlayer("bar", HUMAN), PlayerFactory.createPlayer("foo", HUMAN)))
    //        }
    //      }
    //    }
  }

  "The control round function" should {
    TestUtil.disableDelay()
    //    "throw no exception and return a winner" in {
    //      val players = List(PlayerFactory.createPlayer("foo", HUMAN), PlayerFactory.createPlayer("bar", HUMAN))
    //      val matchImpl = Match(players, 1, 0)
    //      TestUtil.disableDebugMode()
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("1\n1\n1\n") {
    //          RoundControl.controlRound(matchImpl).winner should be (players.head).or(be (players(1)))
    //        }
    //      }
    //    }
    //    "throw no exception and return a winner if both players stay in" in {
    //      val players = List(PlayerFactory.createPlayer("foo", HUMAN), PlayerFactory.createPlayer("bar", HUMAN))
    //      val matchImpl = Match(players, startingPlayer = 0)
    //      TestUtil.enableDebugMode()
    //      CardManager.shuffleAndReset()
    //      CardManager.resetOrder()
    //      
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n") {
    //          RoundControl.controlRound(matchImpl).winner should be(players.head).or(be(players(1)))
    //        }
    //      }
    //    }
    //  }

    //  "The next round function" should {
    //    TestUtil.disableDelay()
    //    "return null if the match is over" in {
    //      val players = List(PlayerFactory.createPlayer("foo", HUMAN))
    //      val matchImpl = Match(players, 2, 0)
    //      TestUtil.enableDebugMode()
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("1\n1\n1\n") {
    //          RoundControl.controlRound(matchImpl)
    //          RoundControl.nextRound(matchImpl) should be (null)
    //        }
    //      }
    //    }
    //  }

    //  "The next trick function" should {
    //    TestUtil.disableDelay()
    //    "return null if the round is over" in {
    //      val players = List(PlayerFactory.createPlayer("foo", HUMAN))
    //      val matchImpl = Match(players, 2, startingPlayer = 0)
    //      TestUtil.enableDebugMode()
    //      TestUtil.cancelOut() {
    //        TestUtil.simulateInput("1\n1\n1\n") {
    //          val round = RoundControl.controlRound(matchImpl)
    //          TrickControl.nextTrick(round) should be (null)
    //        }
    //      }
    //    }
    //  }
    "The controlSuit function" should {
      TestUtil.disableDelay()
      //    "check if a player can play from the correct suit but doesnt" in {
      //      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      //      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      //      val players = List(player1, player2)
      //      val hand =  Hand(List(Card(CardValue.Ten, Suit.Spades),Card(CardValue.Two, Suit.Hearts)))
      //      player1.provideHand(hand)
      //      val matchImpl = Match(players, 2, startingPlayer = 0)
      //      val round = new Round(Suit.Clubs, matchImpl, players, false)
      //      var trick = new Trick(round)
      //      trick = TrickControl.playCard(trick, round, Card(Ace, Suit.Hearts), player2)._1
      //      TestUtil.enableDebugMode()
      //      TestUtil.cancelOut() {
      //        TestUtil.simulateInput("1\n2\n") {
      //          val card = TrickControl.controlSuitplayed(trick, player1)
      //        }
      //      }
      //    }
      //  }

      "The control Trick function" should {
        TestUtil.disableDelay()
        //    "return the other player if the dog decides not to play" in {
        //      val foo = PlayerFactory.createPlayer("foo", HUMAN)
        //      foo.doglife = true
        //      foo.provideHand(CardManager.createHand(1))
        //      val bar = PlayerFactory.createPlayer("bar", HUMAN)
        //      bar.provideHand(CardManager.createHand(3))
        //      val players = List(foo, bar)
        //      val matchImpl = Match(players, 2, startingPlayer = 0)
        //      TestUtil.enableDebugMode()
        //      val round = new Round(Hearts,matchImpl,players,false)
        //      TestUtil.cancelOut() {
        //        TestUtil.simulateInput("n\n1\n") {
        //          val finalTrick = TrickControl.controlTrick(round = round)
        //          finalTrick.winner should be(bar)
        //        }
        //      }
        //    }
        //    "return the dog if he wins" in {
        //      TestUtil.enableDebugMode()
        //      CardManager.resetOrder()
        //      for (i <- 0 to 12) {
        //        CardManager.nextCard()
        //      }
        //      val foo = PlayerFactory.createPlayer("foo", HUMAN)
        //      foo.doglife = true
        //      foo.provideHand(CardManager.createHand(1))
        //      val bar = PlayerFactory.createPlayer("bar", HUMAN)
        //      bar.provideHand(CardManager.createHand(3))
        //      val players = List(foo, bar)
        //      val matchImpl = Match(players, 2, startingPlayer = 0)
        //      val round = new Round(foo.currentHand().get.cards.head.suit, matchImpl, players, false)
        //      TestUtil.cancelOut() {
        //        TestUtil.simulateInput("y\n1\n") {
        //          val finalTrick = TrickControl.controlTrick(round = round)
        //          finalTrick.winner should be(bar)
        //        }
        //      }
        //    }
      }
    }
  }
}