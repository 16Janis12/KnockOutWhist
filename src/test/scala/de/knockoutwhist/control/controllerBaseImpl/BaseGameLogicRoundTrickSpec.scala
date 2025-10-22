package de.knockoutwhist.control.controllerBaseImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.player.{StubPlayer, AbstractPlayer}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.cards.{Card, CardValue, Suit}
import scala.collection.immutable.HashMap
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{ResultPlayer, RoundResult}

class BaseGameLogicRoundTrickSpec extends AnyWordSpec with Matchers {

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

  "BaseGameLogic endTrick and endRound" should {

    "endTrick adds a trick with correct winner to the current round" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)

      val p1 = new StubPlayer("p1")
      val p2 = new StubPlayer("p2")

      // prepare match and round
      val matchImpl = logic.createMatch(List(p1, p2))
      val round = de.knockoutwhist.rounds.Round(Suit.Spades, firstRound = false)
      logic.currentMatch = Some(matchImpl)
      logic.currentRound = Some(round)

      // trick: p1 plays Ace of Spades (trump), p2 plays King of Hearts
      val c1 = Card(CardValue.Ace, Suit.Spades)
      val c2 = Card(CardValue.King, Suit.Hearts)
      val trick = Trick(cards = HashMap(c1 -> p1, c2 -> p2), winner = None, firstCard = Some(c1))
      logic.currentTrick = Some(trick)

      val newRound = logic.endTrick()
      newRound.tricklist should have length 1
      newRound.tricklist.last.winner shouldBe defined
      newRound.tricklist.last.winner.get shouldBe p1
    }

    "endRound appends resulting round to match and returns updated match" in {
      val config = new TestConfiguration()
      val logic = new BaseGameLogic(config)

      val p1 = new StubPlayer("a")
      val p2 = new StubPlayer("b")

      val matchImpl = logic.createMatch(List(p1, p2))
      val round = de.knockoutwhist.rounds.Round(Suit.Clubs, firstRound = false)
      logic.currentMatch = Some(matchImpl)
      logic.currentRound = Some(round)

      // prepare a roundResult where p1 tricked 2 and p2 tricked 1
      val rp1 = ResultPlayer(p1, 2)
      val rp2 = ResultPlayer(p2, 1)
      val roundResult = RoundResult(winners = List(p1), tricked = List(rp1, rp2), notTricked = Nil)

      val updatedMatch = logic.endRound(p1, roundResult)
      updatedMatch.roundlist should have length 1
      updatedMatch.roundlist.last.winner shouldBe Some(p1)
    }
  }
}

