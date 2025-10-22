package de.knockoutwhist.utils.baseQueue

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.player.StubPlayer

class CustomPlayerBaseQueueSpec extends AnyWordSpec with Matchers {

  "CustomPlayerBaseQueue" should {

    "iterate players and track playersSinceLastReset" in {
      val p1 = new StubPlayer("p1")
      val p2 = new StubPlayer("p2")
      val p3 = new StubPlayer("p3")
      val arr = Array(p1, p2, p3)
      val q = new CustomPlayerBaseQueue(arr, 0)

      q.currentIndex shouldBe 0
      val first = q.nextPlayer()
      first shouldBe p1
      q.currentIndex shouldBe 1
      q.playersSinceLastReset() shouldBe 1

      q.nextPlayer()
      q.nextPlayer()
      q.playersSinceLastReset() shouldBe 3
    }

    "resetAndSetStart positions the start index" in {
      val p1 = new StubPlayer("x")
      val p2 = new StubPlayer("y")
      val q = new CustomPlayerBaseQueue(Array(p1, p2), 0)
      q.resetAndSetStart(p2) shouldBe true
      q.currentIndex shouldBe 1
    }

    "remove decreases the player count" in {
      val p1 = new StubPlayer("r1")
      val p2 = new StubPlayer("r2")
      val q = new CustomPlayerBaseQueue(Array(p1, p2), 0)
      q.remove(p1)
      q.size shouldBe 1
    }
  }
}

