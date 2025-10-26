package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.GameState
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseGameLogicSpec extends AnyWordSpec with Matchers {

  class TestConfiguration extends Configuration {
    override def cardManager = new CardBaseManager()
    override def fileFormatter: FileFormatter = new FileFormatter {
      override def formatName: String = "test"
      override def fileEnding: String = ".test"
      override def createFormat(matchSnapshot: de.knockoutwhist.persistence.MatchSnapshot): Array[Byte] = Array.emptyByteArray
      override def parseFormat(bytes: Array[Byte]): de.knockoutwhist.persistence.MatchSnapshot = throw new UnsupportedOperationException()
    }
    override def uis: Set[de.knockoutwhist.ui.UI] = Set.empty
    override def listener: Set[de.knockoutwhist.utils.events.EventListener] = Set.empty
    override def createRightQueue(players: Array[AbstractPlayer], start: Int = 0) = new CustomPlayerBaseQueue(players, start)
  }

  "BaseGameLogic" should {

    "create session sets state to Lobby" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      logic.createSession()
      logic.getCurrentState shouldBe GameState.Lobby
    }

    "createMatch and providePlayersWithCards deals hands to players" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)

      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)
      val p3 = PlayerFactory.createPlayer("p3", null, Playertype.STUB)

      val players = List(p1, p2, p3)
      val matchImpl = logic.createMatch(players)
      matchImpl.totalplayers shouldBe players

      // deal cards
      logic.providePlayersWithCards()

      players.foreach { p =>
        p.currentHand().isDefined shouldBe true
        p.currentHand().get.cards.size shouldBe matchImpl.numberofcards
      }
    }

    "createSnapshot and restore restores match state" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)

      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)

      val players = List(p1, p2)
      logic.createMatch(players)
      logic.providePlayersWithCards()

      val snapshot = new BaseGameLogicSnapshot(logic)

      val logic2 = new BaseGameLogic(config)
      // restore into a fresh logic instance
      snapshot.restore(logic2)

      logic2.getCurrentMatch shouldBe logic.getCurrentMatch
    }
  }
}

