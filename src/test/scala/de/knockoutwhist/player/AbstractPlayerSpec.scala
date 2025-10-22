package de.knockoutwhist.player

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.cards.{Card, CardValue, Suit, Hand}

import java.util.UUID

class AbstractPlayerSpec extends AnyWordSpec with Matchers {

  "AbstractPlayer (via StubPlayer)" should {
    "provide and remove hand correctly" in {
      val p = new StubPlayer("testplayer")
      val c1 = Card(CardValue.Ace, Suit.Spades)
      val c2 = Card(CardValue.Ten, Suit.Hearts)
      p.currentHand() shouldBe None
      p.provideHand(Hand(List(c1, c2)))
      p.currentHand().isDefined shouldBe true
      p.currentHand().get.cards.size shouldBe 2
      p.removeCard(c1)
      p.currentHand().get.cards should not contain c1
    }

    "set and reset dog life" in {
      val p = new StubPlayer("dawg")
      p.isInDogLife shouldBe false
      p.setDogLife()
      p.isInDogLife shouldBe true
      p.resetDogLife()
      p.isInDogLife shouldBe false
    }

    "generate and receive PlayerData when ids and names match" in {
      val id = UUID.randomUUID()
      val p = new StubPlayer("playerx", id)
      val c = Card(CardValue.King, Suit.Clubs)
      p.provideHand(Hand(List(c)))
      p.setDogLife()
      val data = p.generatePlayerData()

      // receiving back into same player should succeed
      p.receivePlayerData(data)
      p.currentHand().isDefined shouldBe true
      p.currentHand().get.cards.head shouldBe c
    }

    "receivePlayerData throws when id or name mismatch" in {
      val p = new StubPlayer("alice")
      val otherId = UUID.randomUUID()
      val dataWithWrongId = PlayerData(otherId, "alice", None, dogLife = false)
      an [IllegalArgumentException] should be thrownBy {
        p.receivePlayerData(dataWithWrongId)
      }

      val p2 = new StubPlayer("bob")
      val dataWithWrongName = PlayerData(p2.id, "notbob", None, dogLife = false)
      an [IllegalArgumentException] should be thrownBy {
        p2.receivePlayerData(dataWithWrongName)
      }
    }
  }
}

