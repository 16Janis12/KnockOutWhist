package de.knockoutwhist.persistence.formats

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.persistence.*

class JSONFormatterSpec extends AnyWordSpec with Matchers {

  "JSONFormatter" should {

    "roundtrip a MatchSnapshot with None gameLogic" in {
      val fmt = new JSONFormatter()
      val snapshot = MatchSnapshot()
      val bytes = fmt.createFormat(snapshot)
      val parsed = fmt.parseFormat(bytes)

      parsed.entryPoint shouldBe None
      parsed.gameLogicSnapShot shouldBe None
    }

    "roundtrip a MatchSnapshot with an entry point" in {
      val fmt = new JSONFormatter()
      val snapshot = MatchSnapshot().withMethodEntryPoint(MethodEntryPoint.ControlMatch)
      val bytes = fmt.createFormat(snapshot)
      val parsed = fmt.parseFormat(bytes)

      parsed.entryPoint shouldBe Some(MethodEntryPoint.ControlMatch)
    }
  }
}

