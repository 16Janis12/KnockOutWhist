package de.knockoutwhist.configuration

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.DefaultConfiguration
import de.knockoutwhist.control.controllerBaseImpl.{MainLogic, MatchLogic, PlayerControl, PlayerLogic, RoundLogic, TrickLogic}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.player.baseImpl.HumanPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

class DefaultConfigurationTests extends AnyWordSpec with Matchers{
  "The Configuration by default" should {
    "select MainLogic for the main component" in {
      DefaultConfiguration.maincomponent shouldBe MainLogic
    }
    "select MatchLogic for the match component" in {
      DefaultConfiguration.matchcomponent shouldBe MatchLogic
    }
    "select PlayerControl for the player control component" in {
      DefaultConfiguration.playeractrcomponent shouldBe PlayerControl
    }
    "select PlayerLogic for the player logic component" in {
      DefaultConfiguration.playerlogcomponent shouldBe PlayerLogic
    }
    "select TrickLogic for the trick component" in {
      DefaultConfiguration.trickcomponent shouldBe TrickLogic
    }
    "select CardBaseManager for the cardManager" in {
      DefaultConfiguration.cardManager shouldBe CardBaseManager
    }
    "select RoundLogic for the round logic component" in {
      DefaultConfiguration.roundlogcomponent shouldBe RoundLogic
    }

    "create a CustomPlayerBaseQueue as the right Queue" in {
      val player1 = PlayerFactory.createPlayer("Peter", HUMAN)
      val queue = new CustomPlayerBaseQueue(Array(player1), 0)
      val rightQueue = DefaultConfiguration.createRightQueue(Array(player1), 0)
      rightQueue.currentIndex shouldBe queue.currentIndex
    }
  }
}
