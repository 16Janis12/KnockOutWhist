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

class BaseGameLogicMoreSpec extends AnyWordSpec with Matchers {

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

  "BaseGameLogic additional behaviors" should {

    "providePlayersWithCards deals 1 card to dogs and normal handSize to others" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)

      val m = logic.createMatch(List(p1, p2))
      // make p2 a dog and set number of cards to 3
      p2.setDogLife()
      logic.currentMatch = Some(m.setNumberOfCards(3))

      logic.providePlayersWithCards()

      p1.currentHand().isDefined shouldBe true
      p2.currentHand().isDefined shouldBe true
      p1.currentHand().get.cards.size shouldBe 3
      p2.currentHand().get.cards.size shouldBe 1
    }

    "endTrick produces Round with appended trick having winner preserved" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)

      val m = logic.createMatch(List(p1, p2))
      logic.playerQueue = Some(cfg.createRightQueue(m.playersIn.toArray, 0))

      val round = Round(Suit.Hearts, firstRound = false)
      logic.currentRound = Some(round)

      // create a trick where p2 should win by trump
      val c1 = Card(CardValue.Two, Suit.Spades)
      val c2 = Card(CardValue.Three, Suit.Hearts)
      val trick = Trick().addCard(c1, p1).addCard(c2, p2).setfirstcard(c1)
      logic.currentTrick = Some(trick)

      val newRound = logic.endTrick()
      newRound.tricklist.size shouldBe 1
      val last = newRound.tricklist.head
      last.winner shouldBe Some(p2)
      last.firstCard shouldBe Some(c1)
    }
  }
}
