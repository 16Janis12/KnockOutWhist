package de.knockoutwhist.player

import de.knockoutwhist.control.MatchControl
import de.knockoutwhist.control.MatchControl.playerQueue
import de.knockoutwhist.player.Playertype.{AI, HUMAN}
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.utils.CustomPlayerQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.Random

class AITests extends AnyWordSpec with Matchers {
  "An AI" should {
    "be able to get created" in {
      val ai1 = PlayerFactory.createPlayer("Robot", AI)
      ai1 shouldBe PlayerFactory.createPlayer("Robot", AI)
    }
    "be able to lay down cards by itself" in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val ai1 = PlayerFactory.createPlayer("Robot", AI)
      val playerarray = Array(player1, ai1)
      val playerlist = List(player1, ai1)
      val match1 = Match(playerlist)
      MatchControl.playerQueue = CustomPlayerQueue[AbstractPlayer](playerarray, Random.nextInt(playerarray.length))
      TestUtil.simulateInput("1\n1\n1\n1\n1\n1\n1\n") {
        MatchControl.controlMatch()
      }
    }
  }


}
