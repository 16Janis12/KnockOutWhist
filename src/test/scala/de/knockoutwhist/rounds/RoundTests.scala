package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.rounds.{Match, Round, Trick}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.cards.Player
import de.knockoutwhist.testutils.TestUtil

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class RoundTests extends AnyWordSpec with Matchers{
  "A Match" should {
    val player1 = Player("Gunter")
    val player2 = Player("Peter")
    val player_list = List(player1, player2)
    val match1 = Match(player_list)
    val round1 = match1.create_round()
    val trumpsuit = round1.trumpSuit
    val trick1 = round1.create_trick()
    TestUtil.simulateInput("1\n")
    KnockOutWhist.matchControl.playerControl.playCard(player1)
    trick1.playCard(KnockOutWhist.matchControl.playerControl.playCard(player1), player1)
    TestUtil.simulateInput("1\n")
    trick1.playCard(KnockOutWhist.matchControl.playerControl.playCard(player2), player2)
    trick1.wonTrick()
    round1.finalizeRound(true) 
    
    
    
    
    
    
    
    
  }
}
