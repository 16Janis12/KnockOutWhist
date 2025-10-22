package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.control.controllerBaseImpl.{BaseGameLogic, BaseGameLogicSnapshot}
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}
import de.knockoutwhist.control.{GameLogic, GameState, SnapshottingGameLogic}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue

class SnapshotUtilSpec extends AnyWordSpec with Matchers {

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

  "SnapshotUtil.generateSnapshots" should {
    "return snapshots for game logic and player tie logic, and game logic snapshot can restore" in {
      // Arrange
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)
      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)
      logic.createMatch(List(p1, p2))
      logic.createSession() // put logic into Lobby to have a known state

      // Act
      val (glSnap, tieSnap) = SnapshotUtil.generateSnapshots(logic)

      // Assert basic types
      glSnap shouldBe a [BaseGameLogicSnapshot]
      tieSnap should not be null
      
      // Act restore
      val logic2 = new BaseGameLogic(cfg)
      glSnap.asInstanceOf[BaseGameLogicSnapshot].restore(logic2)
      
      // Assert restored state
      logic2.state shouldEqual GameState.Lobby
    }
  }
}
