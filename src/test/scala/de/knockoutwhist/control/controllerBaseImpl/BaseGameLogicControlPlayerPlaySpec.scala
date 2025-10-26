package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BaseGameLogicControlPlayerPlaySpec extends AnyWordSpec with Matchers {

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

  private def stub(name: String) = PlayerFactory.createPlayer(name, null, Playertype.STUB)

  private def prepareCompletedTrickRound(p1: AbstractPlayer, p2: AbstractPlayer): (Round, Trick) = {
    val c1 = Card(CardValue.Two, Suit.Spades)
    val c2 = Card(CardValue.Three, Suit.Hearts)
    val trick = Trick().addCard(c1, p1).addCard(c2, p2).setfirstcard(c1)
    val round = Round(Suit.Hearts, firstRound = false)
    (round, trick)
  }

  "BaseGameLogic.controlPlayerPlay" should {

    "early-return to controlTrick when player's hand is None and process endTrick without error" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = stub("p1")
      val p2 = stub("p2")
      val m = logic.createMatch(List(p1, p2)).setNumberOfCards(1)
      logic.currentMatch = Some(m)

      // queue advanced to simulate all players already played -> isOver == true
      val q = cfg.createRightQueue(m.playersIn.toArray, 0)
      q.nextPlayer(); q.nextPlayer()
      logic.playerQueue = Some(q)

      val (round, trick) = prepareCompletedTrickRound(p1, p2)
      logic.currentRound = Some(round)
      logic.currentTrick = Some(trick)

      // set currentPlayer to p1 WITHOUT a hand (None)
      logic.currentPlayer = Some(p1)

      // Act
      logic.controlPlayerPlay()

      // Assert: endTrick should have been processed and round updated with one trick
      logic.getCurrentRound.isDefined shouldBe true
      logic.getCurrentRound.get.tricklist.size shouldBe 1
      logic.getCurrentRound.get.tricklist.head.winner.isDefined shouldBe true
    }

    "early-return to controlTrick when player's hand has no cards and process endTrick without error" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = stub("p1")
      val p2 = stub("p2")
      val m = logic.createMatch(List(p1, p2)).setNumberOfCards(1)
      logic.currentMatch = Some(m)

      val q = cfg.createRightQueue(m.playersIn.toArray, 0)
      q.nextPlayer(); q.nextPlayer()
      logic.playerQueue = Some(q)

      val (round, trick) = prepareCompletedTrickRound(p1, p2)
      logic.currentRound = Some(round)
      logic.currentTrick = Some(trick)

      // Provide empty hand to p1
      p1.provideHand(de.knockoutwhist.cards.Hand(Nil))
      logic.currentPlayer = Some(p1)

      logic.controlPlayerPlay()

      logic.getCurrentRound.isDefined shouldBe true
      logic.getCurrentRound.get.tricklist.size shouldBe 1
      logic.getCurrentRound.get.tricklist.head.winner.isDefined shouldBe true
    }
  }
}
