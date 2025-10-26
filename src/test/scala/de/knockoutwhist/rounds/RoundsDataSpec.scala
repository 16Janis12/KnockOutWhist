package de.knockoutwhist.rounds

import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.player.{PlayerFactory, Playertype}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RoundsDataSpec extends AnyWordSpec with Matchers {

  private def stubPlayer(name: String) = PlayerFactory.createPlayer(name, null, Playertype.STUB)

  "Match" should {
    "playersOut returns players not in playersIn and flags isOver correctly" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val p3 = stubPlayer("p3")
      val m = Match(List(p1, p2, p3), playersIn = List(p1, p2))

      m.playersOut shouldBe List(p3)
      m.isOver shouldBe false

      val m2 = m.updatePlayersIn(List(p1))
      m2.isOver shouldBe true
    }

    "setNumberOfCards and setDogLife update fields and addRound appends" in {
      val p1 = stubPlayer("p1")
      val m = Match(List(p1), playersIn = List(p1))
      val m2 = m.setNumberOfCards(3).setDogLife()
      m2.numberofcards shouldBe 3
      m2.dogLife shouldBe true

      val r = Round(Suit.Spades, firstRound = true)
      val m3 = m2.addRound(r)
      m3.roundlist shouldBe List(r)
    }
  }

  "Round" should {
    "addTrick appends and preserves fields" in {
      val r = Round(Suit.Hearts, firstRound = false)
      val r2 = r.addTrick(Trick())
      r2.tricklist.size shouldBe 1
      r2.trumpSuit shouldBe r.trumpSuit
      r2.firstRound shouldBe r.firstRound
    }
  }

  "Trick" should {
    "addCard adds mapping, setfirstcard sets firstCard, finished reflects winner" in {
      val p1 = stubPlayer("p1")
      val c = Card(CardValue.Ace, Suit.Diamonds)
      val t = Trick().addCard(c, p1).setfirstcard(c)
      t.cards.get(c) shouldBe Some(p1)
      t.firstCard shouldBe Some(c)
      t.finished shouldBe false

      val t2 = t.copy(winner = Some(p1))
      t2.finished shouldBe true
    }
  }
}
