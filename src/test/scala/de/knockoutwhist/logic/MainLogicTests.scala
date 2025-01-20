package de.knockoutwhist.logic

import de.knockoutwhist.cards.CardValue.{Ace, Seven, Six, Ten}
import de.knockoutwhist.cards.Suit.{Clubs, Spades}
import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.MainLogic
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.gui.{GUIMain, GUIThread}
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.shouldBe
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.{immutable, mutable}

class MainLogicTests extends AnyWordSpec with Matchers {

  "playCard" should {
    "check whether the card played was the first card played in the trick and return the correct trick" in {
      val trick1 = Trick()
      val card1 = Card(Ace, Spades)
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      ControlThread.runLater {
        MainLogic.playCard(trick1, card1, player1) shouldBe Trick(firstCard = Some(card1), cards = immutable.HashMap(card1 -> player1))
      }
    }
    "only add the card to the cards HashMap if it is not the first card played" in {
      val firstcard = Card(Ten, Clubs)
      val trick1 = Trick(firstCard = Some(firstcard))
      val card1 = Card(Ace, Spades)
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      ControlThread.runLater {
        MainLogic.playCard(trick1, card1, player1) shouldBe Trick(firstCard = Some(firstcard), cards = immutable.HashMap(card1 -> player1))
      }
    }
  }
  "The Main Logic method" should {
    TestUtil.disableDelay()
    "be able to start the match and ask the players for their names" in {
      ControlThread.runLater {
        MainLogic.startMatch()
      }
    }
    "be able to insert the entered Players into the match data structure start with the trick by asking the player for a card" in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      ControlThread.runLater {
        MainLogic.enteredPlayers(List(player1, player2))
      }
    }
  }
  "controlMatch" should {
    "check if the current match is already finished and if so, show who won etc." in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      val round1 = Round(Suit.Spades, List(), List(player1), List(), 0, player1, false)
      val match1 = Match(List(player1, player2), 5, true, List(round1)) //a finished Match
      ControlThread.runLater {
        MainLogic.controlMatch(match1)
      }
    }
    "check if the current match is already finished and if not, get the remaining players and continue" in {
      val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
      val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
      val round1 = Round(Suit.Spades, List(), List(player1, player2), List(), 0, player1, false)
      val match1 = Match(List(player1, player2), 5, true, List(round1))
      ControlThread.runLater {
        MainLogic.controlMatch(match1)
      }
    }
  }
}
