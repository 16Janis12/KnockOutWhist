package de.knockoutwhist.player

import de.knockoutwhist.cards.CardValue.{Ace, Three, Two}
import de.knockoutwhist.cards.Suit.*
import de.knockoutwhist.cards.{Card, CardManager, Hand}
import de.knockoutwhist.control.{AILogic, MatchControl}
import de.knockoutwhist.control.MatchControl.playerQueue
import de.knockoutwhist.player.Playertype.{AI, HUMAN}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.utils.CustomPlayerQueue
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.util.{Random, Success}

class AITests extends AnyWordSpec with Matchers {
  "An AI" should {
    "be able to get created" in {
      val ai1 = PlayerFactory.createPlayer("Robot", AI)
      ai1 shouldBe PlayerFactory.createPlayer("Robot", AI)
    }
    "be able to lay down cards by itself" in {
      TestUtil.enableDebugMode()
      TestUtil.disableDelay()
      CardManager.resetOrder()
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer("Robot2", AI)
      val playerarray = Array(ai1, ai2)
      val playerlist = List(ai1, ai2)
      val match1 = Match(playerlist)
      MatchControl.playerQueue = CustomPlayerQueue[AbstractPlayer](playerarray, Random.nextInt(playerarray.length))
      MatchControl.controlMatch() shouldBe ai2
    }
    "request a tie card" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      ai1.handlePickTieCard(1, 2) match
        case Success(i: Int) => i should be(1).or(be(2))
        case scala.util.Failure(_) => fail("AI should return a card")
    }
    "calculate which card to use in case of a used trump (can overrule)" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer("Robot2", AI)
      val playerList = List(ai1,ai2)
      val round = new Round(Hearts, Match(playerList), playerList, firstRound = false)
      round.remainingTricks = 5
      val playedCards = mutable.HashMap[Card, AbstractPlayer]()
      playedCards.put(Card(Two,Hearts), ai1)
      val trick = Trick(round = round, cards = playedCards)
      trick.setfirstcard(Card(Two,Spades))
      trick.remainingPlayers = 2
      ai2.provideHand(Hand(List(Card(Three, Hearts))))
      AILogic.decideCard(ai2, trick) shouldBe Card(Three, Hearts)
    }
    "calculate which card to use in case of a used trump (can't overrule)" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer("Robot2", AI)
      val playerList = List(ai1, ai2)
      val round = new Round(Hearts, Match(playerList), playerList, firstRound = false)
      round.remainingTricks = 5
      val playedCards = mutable.HashMap[Card, AbstractPlayer]()
      playedCards.put(Card(Three, Hearts), ai1)
      val trick = Trick(round = round, cards = playedCards)
      trick.setfirstcard(Card(Two, Spades))
      trick.remainingPlayers = 2
      ai2.provideHand(Hand(List(Card(Two, Hearts), Card(Ace, Diamonds))))
      AILogic.decideCard(ai2, trick) shouldBe Card(Ace, Diamonds)
    }
    "calculate which card to use in case of a used trump (risk)" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer("Robot2", AI)
      val playerList = List(ai1, ai2, ai1, ai1, ai1)
      val round = new Round(Hearts, Match(playerList), playerList, firstRound = false)
      round.remainingTricks = 5

      val playedCards = mutable.HashMap[Card, AbstractPlayer]()
      playedCards.put(Card(Two, Hearts), ai1)
      val trick = Trick(round = round, cards = playedCards)
      trick.setfirstcard(Card(Two, Spades))
      trick.remainingPlayers = 2
      ai2.provideHand(Hand(List(Card(Three, Hearts), Card(Ace, Hearts))))
      AILogic.decideCard(ai2, trick) shouldBe Card(Three, Hearts)
    }
    "calculate which card to use in case of a used trump (can't overrule, discard trump)" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer(playertype = AI)
      val playerList = List(ai1, ai2)
      val round = new Round(Hearts, Match(playerList), playerList, firstRound = false)
      round.remainingTricks = 5
      val playedCards = mutable.HashMap[Card, AbstractPlayer]()
      playedCards.put(Card(Three, Hearts), ai1)
      val trick = Trick(round = round, cards = playedCards)
      trick.setfirstcard(Card(Two, Spades))
      trick.remainingPlayers = 2
      ai2.provideHand(Hand(List(Card(Two, Hearts))))
      AILogic.decideCard(ai2, trick) shouldBe Card(Two, Hearts)
    }
    "calculate which card to use in case of a used trump (can overrule as dog)" in {
      val ai1 = PlayerFactory.createPlayer("Robot1", AI)
      val ai2 = PlayerFactory.createPlayer("Robot2", AI)
      ai2.doglife = true
      val playerList = List(ai1,ai2)
      val round = new Round(Hearts, Match(playerList), playerList, firstRound = false)
      round.remainingTricks = 5
      val playedCards = mutable.HashMap[Card, AbstractPlayer]()
      playedCards.put(Card(Two,Hearts), ai1)
      val trick = Trick(round = round, cards = playedCards)
      trick.setfirstcard(Card(Two,Spades))
      trick.remainingPlayers = 2
      ai2.provideHand(Hand(List(Card(Three, Hearts))))
      AILogic.decideDogCard(ai2, trick, false) shouldBe Some(Card(Three, Hearts))
    }

  }


}
