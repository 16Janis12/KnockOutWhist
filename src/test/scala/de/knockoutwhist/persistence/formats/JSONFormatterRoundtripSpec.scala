package de.knockoutwhist.persistence.formats

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.components.Configuration
import de.knockoutwhist.persistence.MatchSnapshot
import de.knockoutwhist.persistence.MethodEntryPoint
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.player.PlayerFactory
import de.knockoutwhist.player.Playertype
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.player.AbstractPlayer

class JSONFormatterRoundtripSpec extends AnyWordSpec with Matchers {

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
    override def createRightQueue(players: Array[AbstractPlayer], start: Int = 0) = new de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue(players, start)
  }

  "JSONFormatter integration" should {
    "serialize and parse a BaseGameLogic snapshot" in {
      val cfg = new TestConfiguration()
      val logic = new BaseGameLogic(cfg)

      // create players
      val p1 = PlayerFactory.createPlayer("p1", null, Playertype.STUB)
      val p2 = PlayerFactory.createPlayer("p2", null, Playertype.STUB)

      logic.createMatch(List(p1, p2))
      logic.providePlayersWithCards()

      val snap = logic.createSnapshot()
      val matchSnapshot = MatchSnapshot().withMethodEntryPoint(MethodEntryPoint.ControlMatch).withGameLogicSnapShot(snap.asInstanceOf[de.knockoutwhist.control.LogicSnapshot[de.knockoutwhist.control.GameLogic]])

      val fmt = new JSONFormatter()
      val bytes = fmt.createFormat(matchSnapshot)

      val parsed = fmt.parseFormat(bytes)
      parsed.entryPoint shouldBe defined
      parsed.gameLogicSnapShot shouldBe defined

      // restore into fresh logic
      val logic2 = new BaseGameLogic(cfg)
      parsed.gameLogicSnapShot.get.restore(logic2)

      logic2.getCurrentMatch shouldBe defined
      logic2.getCurrentMatch.get.totalplayers.map(_.name) should contain allElementsOf List(p1.name, p2.name)
    }
  }
}
