package de.knockoutwhist.control.controllerBaseImpl.sublogic

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.persistence.MethodEntryPoint
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic

class BasePersistenceManagerSpec extends AnyWordSpec with Matchers {

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

  "BasePersistenceManager" should {

    "update attaches a gameLogic snapshot and entry point" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val pm = new BasePersistenceManager(logic)

      val snap = pm.update(MethodEntryPoint.ControlMatch)
      snap.entryPoint shouldBe Some(MethodEntryPoint.ControlMatch)
      snap.gameLogicSnapShot.isDefined shouldBe true
    }
  }
}

