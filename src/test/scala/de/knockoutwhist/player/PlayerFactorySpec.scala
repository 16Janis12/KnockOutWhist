package de.knockoutwhist.player

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class PlayerFactorySpec extends AnyWordSpec with Matchers {

  "PlayerFactory" should {

    "create Stub and Human players and preserve provided UUIDs" in {
      val id1 = UUID.randomUUID()
      val id2 = UUID.randomUUID()

      val s = PlayerFactory.createPlayer("stub", id1, Playertype.STUB)
      val h = PlayerFactory.createPlayer("human", id2, Playertype.HUMAN)

      s.id shouldBe id1
      h.id shouldBe id2

      PlayerFactory.parsePlayerType(s) shouldBe Playertype.STUB
      PlayerFactory.parsePlayerType(h) shouldBe Playertype.HUMAN
    }

    "create with null name generates a random name but still correct type" in {
      val p = PlayerFactory.createPlayer(null, null, Playertype.STUB)
      p.name.length should be > 0
      PlayerFactory.parsePlayerType(p) shouldBe Playertype.STUB
    }
  }
}
