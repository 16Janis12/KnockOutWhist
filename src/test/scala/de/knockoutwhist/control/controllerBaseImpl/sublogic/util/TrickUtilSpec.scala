package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.player.{PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TrickUtilSpec extends AnyWordSpec with Matchers {

  private def stubPlayer(name: String) = PlayerFactory.createPlayer(name, null, Playertype.STUB)

  "TrickUtil" should {

    "isOver returns true when queue passed all players" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2))
      val q = new CustomPlayerBaseQueue(Array(p1, p2), 0)

      // simulate two turns
      q.nextPlayer()
      q.nextPlayer()

      TrickUtil.isOver(m, q) shouldBe true
    }

    "isOver returns false when not all players have played" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2))
      val q = new CustomPlayerBaseQueue(Array(p1, p2), 0)

      // one turn only
      q.nextPlayer()

      TrickUtil.isOver(m, q) shouldBe false
    }

    "finishTrick determines winner using trump suit when present" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val round = Round(Suit.Hearts, firstRound = false)
      val c1 = Card(CardValue.Two, Suit.Spades)
      val c2 = Card(CardValue.Three, Suit.Hearts) // trump
      val trick = Trick().addCard(c1, p1).addCard(c2, p2).setfirstcard(c1)

      val res = TrickUtil.finishTrick(trick, round)
      res.winner shouldBe p2
    }

    "finishTrick determines winner by highest of first card suit when no trump played" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val round = Round(Suit.Clubs, firstRound = false)
      val c1 = Card(CardValue.Two, Suit.Spades)
      val c2 = Card(CardValue.Ace, Suit.Spades)
      val trick = Trick().addCard(c1, p1).addCard(c2, p2).setfirstcard(c1)

      val res = TrickUtil.finishTrick(trick, round)
      res.winner shouldBe p2
    }
  }
}
