package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.{AbstractPlayer, StubPlayer}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BasePlayerInputLogicSpec extends AnyWordSpec with Matchers {

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

  "BasePlayerInputLogic" should {

    "request methods set waiting flags" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val inputLogic = logic.playerInputLogic.asInstanceOf[BasePlayerInputLogic]

      val p = new StubPlayer("p")
      // initially not waiting
      inputLogic.isWaitingForInput shouldBe false

      inputLogic.requestCard(p)
      inputLogic.isWaitingForInput shouldBe true

      inputLogic.requestTrumpSuit(p)
      inputLogic.isWaitingForInput shouldBe true
    }
  }
}

