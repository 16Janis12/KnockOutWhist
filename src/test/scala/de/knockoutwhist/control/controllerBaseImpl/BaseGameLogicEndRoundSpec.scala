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

class BaseGameLogicEndRoundSpec extends AnyWordSpec with Matchers {

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

  "BaseGameLogic.endRound" should {

    "when dogLife is false: set dog life on match and on notTricked players" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = stub("p1")
      val p2 = stub("p2")
      val p3 = stub("p3")

      val m = logic.createMatch(List(p1, p2, p3))
      logic.currentRound = Some(Round(Suit.Spades, firstRound = false))

      // create a RoundResult-like input: winners=p1, notTricked contains p2 and p3
      val trick1 = Trick(winner = Some(p1))
      val trick2 = Trick(winner = Some(p1))
      logic.currentRound = Some(Round(Suit.Spades, firstRound = false, tricklist = List(trick1, trick2)))

      val res = de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundUtil.finishRound(logic.currentRound.get, m)
      val newMatch = logic.endRound(p1, res)

      // match should now be in dogLife mode and p2/p3 should be flagged
      newMatch.dogLife shouldBe true
      p2.isInDogLife shouldBe true
      p3.isInDogLife shouldBe true
      // winners reset dog life
      p1.isInDogLife shouldBe false
    }

    "when dogLife is true: remove notTricked players from playersIn" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      val p1 = stub("p1")
      val p2 = stub("p2")
      val p3 = stub("p3")

      val baseMatch = logic.createMatch(List(p1, p2, p3))
      // set dogLife true in match
      logic.currentMatch = Some(baseMatch.setDogLife())

      val trick1 = Trick(winner = Some(p1))
      logic.currentRound = Some(Round(Suit.Hearts, firstRound = false, tricklist = List(trick1)))

      val res = de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundUtil.finishRound(logic.currentRound.get, logic.currentMatch.get)
      val newMatch = logic.endRound(p1, res)

      // Players that did not win any trick should be removed
      newMatch.playersIn shouldBe List(p1)
      newMatch.playersOut should contain allOf (p2, p3)
    }
  }
}
