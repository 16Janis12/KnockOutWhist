package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.GameState
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseGameLogicStateSpec extends AnyWordSpec with Matchers {

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

  "BaseGameLogic.isWaitingForInput" should {
    "be false in MainMenu and Lobby by default" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      // Initially MainMenu
      logic.getCurrentState shouldBe GameState.MainMenu
      logic.isWaitingForInput shouldBe false

      logic.createSession()
      logic.getCurrentState shouldBe GameState.Lobby
      logic.isWaitingForInput shouldBe false
    }

    "be false by default in InGame and TieBreak without pending input" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      // Force state changes directly via snapshot/restore to avoid UI/input
      val snap = new BaseGameLogicSnapshot(
        savedState = GameState.InGame,
        cardContainer = None,
        cardIndex = None,
        currentMatch = None,
        currentRound = None,
        currentTrick = None,
        currentPlayer = None,
        playerIndex = None,
        players = None,
        playerStates = Map.empty
      )
      snap.restore(logic)
      logic.getCurrentState shouldBe GameState.InGame
      logic.isWaitingForInput shouldBe false

      val snap2 = new BaseGameLogicSnapshot(
        savedState = GameState.TieBreak,
        cardContainer = None,
        cardIndex = None,
        currentMatch = None,
        currentRound = None,
        currentTrick = None,
        currentPlayer = None,
        playerIndex = None,
        players = None,
        playerStates = Map.empty
      )
      snap2.restore(logic)
      logic.getCurrentState shouldBe GameState.TieBreak
      logic.isWaitingForInput shouldBe false
    }
  }
}
