package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.{AbstractPlayer, StubPlayer}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BasePlayerTieLogicSpec extends AnyWordSpec with Matchers {

  class TestCardManager extends CardBaseManager() {
    // Do not shuffle for deterministic behavior
    override def shuffleAndReset(): Unit = {}
    override def removeCards(amount: Int): List[Card] = {
      // Return a single card deterministically derived from amount
      val cv = CardValue.fromOrdinal(amount % CardValue.values.length)
      List(Card(cv, Suit.Spades))
    }
  }

  class TestConfiguration extends Configuration {
    override def cardManager = new TestCardManager()
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

  "BasePlayerTieLogic" should {

    "initialize tie and request first player's choice" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val tieLogic = logic.playerTieLogic.asInstanceOf[BasePlayerTieLogic]

      val p1 = new StubPlayer("p1")
      val p2 = new StubPlayer("p2")
      val rr = RoundResult(List(p1, p2), Nil, Nil)

      tieLogic.handleTie(rr)

      tieLogic.getTiedPlayers should contain inOrder (p1, p2)
      tieLogic.getTieBreakerIndex shouldBe 0
      tieLogic.isWaitingForInput shouldBe true
      tieLogic.currentTiePlayer() shouldBe p1
      tieLogic.highestAllowedNumber() shouldBe (logic.cardManager.get.remainingCards - (2 - 0 - 1))
    }

    "process received tie-breaker cards and determine a single winner" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val tieLogic = logic.playerTieLogic.asInstanceOf[BasePlayerTieLogic]

      val p1 = new StubPlayer("a")
      val p2 = new StubPlayer("b")
      val rr = RoundResult(List(p1, p2), Nil, Nil)

      tieLogic.handleTie(rr)

      // First player picks card derived from number 3
      tieLogic.receivedTieBreakerCard(3)
      // After selection, still waiting for next player's input
      tieLogic.isWaitingForInput shouldBe true
      tieLogic.getSelectedCard.size shouldBe 1

      // Second player picks a higher card derived from number 5
      tieLogic.receivedTieBreakerCard(5)

      // After both selections the tie should be resolved (since cards are different)
      tieLogic.getTiedPlayers shouldBe Nil
      tieLogic.getSelectedCard shouldBe Map.empty
      tieLogic.isWaitingForInput shouldBe false
      tieLogic.getRoundResult shouldBe None
    }

    "snapshot and restore preserves tie logic state" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)
      val tieLogic = logic.playerTieLogic.asInstanceOf[BasePlayerTieLogic]

      val p1 = new StubPlayer("x")
      val p2 = new StubPlayer("y")
      val rr = RoundResult(List(p1, p2), Nil, Nil)

      tieLogic.handleTie(rr)
      // first player selected one card
      tieLogic.receivedTieBreakerCard(2)

      val snapshot = tieLogic.createSnapshot().asInstanceOf[BasePlayerTieLogicSnapshot]

      // Create fresh logic and tie logic to restore into
      val logic2 = new BaseGameLogic(config)
      val tieLogic2 = new BasePlayerTieLogic(logic2)

      // Restore snapshot
      snapshot.restore(tieLogic2)

      tieLogic2.getTiedPlayers should contain theSameElementsInOrderAs tieLogic.getTiedPlayers
      tieLogic2.getSelectedCard.size shouldBe tieLogic.getSelectedCard.size
      tieLogic2.getLastNumber shouldBe tieLogic.getLastNumber
      tieLogic2.getTieBreakerIndex shouldBe tieLogic.getTieBreakerIndex
    }
  }
}
