package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.Suit.Spades
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.{AbstractPlayer, StubPlayer}
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap

class BasePlayerInputLogicReceivedSpec extends AnyWordSpec with Matchers {

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

  "BasePlayerInputLogic received methods" should {

    "receivedCard updates currentTrick and removes card from player's hand" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = new StubPlayer("p1")
      val p2 = new StubPlayer("p2")

      // give p1 a hand with two cards
      val c1 = Card(CardValue.Ace, Suit.Spades)
      val c2 = Card(CardValue.King, Suit.Hearts)
      p1.provideHand(de.knockoutwhist.cards.Hand(List(c1, c2)))

      // setup match and queue
      val matchImpl = logic.createMatch(List(p1, p2)).setNumberOfCards(1)
      logic.playerQueue = Some(cfg.createRightQueue(matchImpl.playersIn.toArray, 0))

      // set current trick and player
      val trick = Trick()
      logic.currentTrick = Some(trick)
      logic.currentPlayer = Some(p1)

      val inputLogic = logic.playerInputLogic.asInstanceOf[BasePlayerInputLogic]
      inputLogic.receivedCard(c1)

      // player's hand should have removed c1
      p1.currentHand().get.cards should not contain c1
      // currentTrick should contain the played card mapping (or have been updated)
      logic.currentTrick.get.cards.keys.exists(card => card == c1) shouldBe true
    }

    "receivedDog handles optional dog card and removes it from player's hand" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = new StubPlayer("p1")
      val p2 = new StubPlayer("p2")

      val dc = Card(CardValue.Two, Suit.Clubs)
      p1.provideHand(de.knockoutwhist.cards.Hand(List(dc)))

      val matchImpl = logic.createMatch(List(p1, p2)).setNumberOfCards(1)
      logic.currentMatch = Some(matchImpl)
      logic.playerQueue = Some(cfg.createRightQueue(matchImpl.playersIn.toArray, 0))

      val trick = Trick().addCard(dc, p2)

      logic.currentTrick = Some(trick)
      logic.currentPlayer = Some(p1)

      val round = Round(Spades, false)
      logic.currentRound = Some(round)


      val inputLogic = logic.playerInputLogic.asInstanceOf[BasePlayerInputLogic]
      inputLogic.receivedDog(Some(dc))

      p1.currentHand().get.cards should not contain dc
      logic.currentTrick.get.cards.keys.exists(card => card == dc) shouldBe true
    }
  }
}

