package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.player.{PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class RoundUtilSpec extends AnyWordSpec with Matchers {

  private def stubPlayer(name: String) = PlayerFactory.createPlayer(name, null, Playertype.STUB)

  "RoundUtil" should {

    "createRound sets fields as provided" in {
      val r = RoundUtil.createRound(Suit.Spades, firstRound = true)
      r.trumpSuit shouldBe Suit.Spades
      r.firstRound shouldBe true
      r.tricklist shouldBe Nil
      r.winner shouldBe None
    }

    "finishRound yields single winner, tricked and notTricked correctly" in {
      // Arrange
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val p3 = stubPlayer("p3")
      val m = Match(List(p1, p2, p3), playersIn = List(p1, p2, p3))

      val cA = Card(CardValue.Ace, Suit.Spades)
      val cK = Card(CardValue.King, Suit.Spades)

      val t1 = Trick().addCard(cA, p1)
      val t2 = Trick().addCard(cK, p1)

      val round = Round(Suit.Spades, firstRound = false, tricklist = List(
        t1.copy(winner = Some(p1)),
        t2.copy(winner = Some(p1))
      ))

      // Act
      val res = RoundUtil.finishRound(round, m)

      // Assert
      res.isTie shouldBe false
      res.winners shouldBe List(p1)
      res.tricked.exists(rp => rp.player == p1 && rp.amountOfTricks == 2) shouldBe true
      res.notTricked should contain allElementsOf List(p2, p3)
    }

    "finishRound detects tie when multiple players share max" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2))

      val r = Round(Suit.Clubs, firstRound = false, tricklist = List(
        Trick(winner = Some(p1)),
        Trick(winner = Some(p2))
      ))

      val res = RoundUtil.finishRound(r, m)
      res.isTie shouldBe true
      res.winners.toSet shouldBe Set(p1, p2)
    }

    "roundEndSnapshot sets the winner on the returned round" in {
      val p1 = stubPlayer("p1")
      val base = Round(Suit.Diamonds, firstRound = true)
      val ended = RoundUtil.roundEndSnapshot(p1, base)
      ended.winner shouldBe Some(p1)
      ended.trumpSuit shouldBe base.trumpSuit
      ended.firstRound shouldBe base.firstRound
    }
  }
}
