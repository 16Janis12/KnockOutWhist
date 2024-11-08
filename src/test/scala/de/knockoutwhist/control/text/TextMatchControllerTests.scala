package de.knockoutwhist.control.text

import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Match
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


}
