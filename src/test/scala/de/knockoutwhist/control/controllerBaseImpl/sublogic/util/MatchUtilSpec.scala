package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.{PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MatchUtilSpec extends AnyWordSpec with Matchers {

  private def stubPlayer(name: String) = PlayerFactory.createPlayer(name, null, Playertype.STUB)

  "MatchUtil" should {

    "isRoundOver true when tricklist size equals numberofcards" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2), numberofcards = 2)
      val r = Round(Suit.Spades, firstRound = false, tricklist = List(Trick(), Trick()))
      MatchUtil.isRoundOver(m, r) shouldBe true
    }

    "isRoundOver false when remaining rounds > 0" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2), numberofcards = 3)
      val r = Round(Suit.Spades, firstRound = false, tricklist = List(Trick()))
      MatchUtil.isRoundOver(m, r) shouldBe false
    }

    "dogNeedsToPlay true when exactly one trick remains" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2), numberofcards = 2)
      val r = Round(Suit.Hearts, firstRound = false, tricklist = List(Trick()))
      MatchUtil.dogNeedsToPlay(m, r) shouldBe true
    }

    "dogNeedsToPlay false otherwise" in {
      val p1 = stubPlayer("p1")
      val p2 = stubPlayer("p2")
      val m = Match(List(p1, p2), playersIn = List(p1, p2), numberofcards = 4)
      val r = Round(Suit.Hearts, firstRound = false, tricklist = List(Trick(), Trick()))
      MatchUtil.dogNeedsToPlay(m, r) shouldBe false
    }
  }
}
