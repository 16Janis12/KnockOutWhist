package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.undo.Command
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.concurrent.atomic.AtomicInteger

class BaseUndoManagerSpec extends AnyWordSpec with Matchers {

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

  "BaseUndoManager" should {

    "do, undo and redo commands via ControlThread" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val um = new BaseUndoManager(logic)

      val doCounter = new AtomicInteger(0)
      val undoCounter = new AtomicInteger(0)

      val cmd = new Command {
        override def doStep(gameLogic: de.knockoutwhist.control.GameLogic): Unit = doCounter.incrementAndGet()
        override def undoStep(gameLogic: de.knockoutwhist.control.GameLogic): Unit = undoCounter.incrementAndGet()
      }

      um.doStep(cmd)
      doCounter.get() shouldBe 1

      // undo (synchronous via TestControlThread)
      um.undoStep()
      undoCounter.get() shouldBe 1

      // redo
      um.redoStep()
      doCounter.get() shouldBe 2
    }
  }
}

