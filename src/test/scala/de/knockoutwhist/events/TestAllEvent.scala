package de.knockoutwhist.events

import de.knockoutwhist.cards.*
import de.knockoutwhist.cards.CardValue.Two
import de.knockoutwhist.cards.Suit.Hearts
import de.knockoutwhist.events.ERROR_STATUS.*
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.ROUND_STATUS.*
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestDogPlayCardEvent, RequestTieNumberEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.events.{EventHandler, SimpleEvent}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

class TestAllEvent extends AnyWordSpec with Matchers {

  private val eventHandler = new EventHandler() {}
  eventHandler.addListener(TUIMain)

  "The render hand event" should {
    TestUtil.disableDelay()
    CardManager.resetOrder()
    val event1 = RenderHandEvent(CardManager.createHand(1), true)
    val event2 = RenderHandEvent(CardManager.createHand(1), false)
    "be able to be created" in {
      event1 should not be null
    }
    "have the correct id" in {
      event1.id should be ("RenderHandEvent")
    }
    "have the correct parameter (showing numbers)" in {
      event1.showNumbers should be (true)
    }
    "have the correct parameter (not showing numbers)" in {
      event2.showNumbers should be (false)
    }
    "be able to be handled" in {
      TestUtil.cancelOut() {
        eventHandler.invoke(event1) should be(true)
        eventHandler.invoke(event2) should be(true)
      }
    }
  }
  "The show tie cards event" should {
    TestUtil.disableDelay()
    val event = ShowTieCardsEvent(List((PlayerFactory.createPlayer("Foo", HUMAN), Card(Two,Suit.Hearts))))
    "be able to be created" in {
      event should not be null
    }
    "have the correct id" in {
      event.id should be ("ShowTieCardsEvent")
    }
    "have the correct player" in {
      event.card.head._1 should be (PlayerFactory.createPlayer("Foo", HUMAN))
    }
    "have the correct card" in {
      event.card.head._2.cardValue should be (Two)
      event.card.head._2.suit should be (Suit.Hearts)
    }
    "be able to be handled" in {
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
  }

  "The request card event" should {
    TestUtil.disableDelay()
    val hand = CardManager.createHand(1)
    val event = RequestCardEvent(hand)
    "be able to be created" in {
      event should not be null
    }
    "have the correct id" in {
      event.id should be("RequestCardEvent")
    }
    "have the correct hand" in {
      event.hand should be(hand)
    }
    "be able to be handled" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("0\n") {
          eventHandler.invoke(event)
        } shouldBe a [Failure[?]]
        TestUtil.simulateInput("5\n") {
          eventHandler.invoke(event)
        } shouldBe a[Failure[?]]
        val result = TestUtil.simulateInput("1\n") {
          eventHandler.invoke(event)
        }
        result should be (Success(hand.cards.head))
      }
    }
  }

  "The request number event" should {
    TestUtil.disableDelay()
    val event = RequestTieNumberEvent(6, 12)
    "be able to be created" in {
      event should not be null
    }
    "have the correct id" in {
      event.id should be("RequestNumberEvent")
    }
    "have the correct min & max" in {
      event.min should be(6)
      event.max should be(12)
    }
    "be able to be handled" in {
      TestUtil.cancelOut() {
        TestUtil.simulateInput("0\n") {
          eventHandler.invoke(event)
        } shouldBe a [Failure[?]]
        TestUtil.simulateInput("28\n") {
          eventHandler.invoke(event)
        } shouldBe a[Failure[?]]
        val result = TestUtil.simulateInput("8\n") {
          eventHandler.invoke(event)
        }
        result should be (Success(8))
      }
    }
  }

  "The RequestDogPlayCardEvent" should {
    TestUtil.disableDelay()
    val hand = Hand(List(Card(CardValue.Ten, Suit.Spades)))
    val event = RequestDogPlayCardEvent(hand, true)
    "be able to get created" in {
      event should not be null
    }
    "have the correct ID" in {
      event.id  should be("RequestDogPlayCardEvent")
    }
    "return the card played if it was played" in {
      TestUtil.simulateInput("y\n") {
        eventHandler.invoke(event)
      } shouldBe a[Success[?]]
    }
    "return an exception if player doesn't want to play but has to" in {
        TestUtil.simulateInput("n\n") {
          eventHandler.invoke(event)
      } shouldBe a [Failure[?]]
    }
    "return None when a player doesn't play a card and is able to do so" in {
      val hand2 = Hand(List(Card(CardValue.Ten, Suit.Spades), Card(CardValue.Nine, Suit.Spades)))
      val event2 = RequestDogPlayCardEvent(hand2, false)
      TestUtil.simulateInput("n\n") {
        eventHandler.invoke(event2)
      } shouldBe a [Success[?]]
    }
  }
  "The RequestPickTrumpsuitEvent" should {
    TestUtil.disableDelay()
    val event = RequestPickTrumpsuitEvent()
    "be able to get created" in {
      event should not be null
    }
    "have an ID" in {
      event.id should be("RequestPickTrumpsuitEvent")
    }
    "be a Success if the player picks a trumpsuit" in {
      TestUtil.simulateInput("1\n") {
        eventHandler.invoke(event)
      } shouldBe a [Success[?]]
      TestUtil.simulateInput("2\n") {
        eventHandler.invoke(event)
      } shouldBe a[Success[?]]
      TestUtil.simulateInput("3\n") {
        eventHandler.invoke(event)
      } shouldBe a[Success[?]]
      TestUtil.simulateInput("4\n") {
        eventHandler.invoke(event)
      } shouldBe a[Success[?]]
    }
    "fail if the player doesn't pick a trumpsuit" in {
      TestUtil.simulateInput("5\n") {
        eventHandler.invoke(event)
      } shouldBe a [Failure[?]]
    }
  }
  "The ShowCurrentTrickEvent" should {
    TestUtil.disableDelay()
    val player1 = PlayerFactory.createPlayer("Gunter", HUMAN)
    val player2 = PlayerFactory.createPlayer("Peter", HUMAN)
    val listplayers = List(player1, player2)
    val match1 = Match(listplayers)
    val round = RoundControl.createround(match1)
    var trick = TrickControl.createtrick(round)
    trick = TrickControl.playCard(trick, round, Card(CardValue.Ten, Suit.Spades), player1)._1
    val event = ShowCurrentTrickEvent(round, trick)
    "be able to get created" in {
      event should not be null
    }
    "have an ID" in {
      event.id should be("ShowCurrentTrickEvent")
    }
    "should be true if a trick can be displayed" in {
      eventHandler.invoke(event) shouldBe true
    }
  }

  "The show global status event" should {
    TestUtil.disableDelay()
    var event: ShowGlobalStatus = null
    "be able to be created" in {
      event = ShowGlobalStatus(SHOW_TIE)
      event should not be null
    }
    "have the correct id" in {
      event = ShowGlobalStatus(SHOW_TIE)
      event.id should be ("ShowGlobalStatus")
    }
    "have the correct status" in {
      event = ShowGlobalStatus(SHOW_TIE)
      event.status should be (SHOW_TIE)
    }
    "be able to be handled with status SHOW_TIE" in {
      event = ShowGlobalStatus(SHOW_TIE)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status SHOW_TIE_WINNER if arguments mismatch" in {
      event = ShowGlobalStatus(SHOW_TIE_WINNER)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status SHOW_TIE_WINNER if arguments match" in {
      event = ShowGlobalStatus(SHOW_TIE_WINNER, PlayerFactory.createPlayer("Foo", HUMAN))
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_TIE_TIE" in {
      event = ShowGlobalStatus(SHOW_TIE_TIE)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_START_MATCH" in {
      event = ShowGlobalStatus(SHOW_START_MATCH)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_TYPE_PLAYERS" in {
      event = ShowGlobalStatus(SHOW_TYPE_PLAYERS)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status SHOW_FINISHED_MATCH if arguments mismatch" in {
      event = ShowGlobalStatus(SHOW_FINISHED_MATCH)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status SHOW_FINISHED_MATCH if arguments match" in {
      event = ShowGlobalStatus(SHOW_FINISHED_MATCH, PlayerFactory.createPlayer("Foo", HUMAN))
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
  }

  "The show player status event" should {
    TestUtil.disableDelay()
    val player = PlayerFactory.createPlayer("Foo", HUMAN)
    var event: ShowPlayerStatus = null
    "be able to be created" in {
      event = ShowPlayerStatus(SHOW_TURN, player)
      event should not be null
    }
    "have the correct id" in {
      event = ShowPlayerStatus(SHOW_TURN, player)
      event.id should be ("ShowPlayerStatus")
    }
    "have the correct status" in {
      event = ShowPlayerStatus(SHOW_TURN, player)
      event.status should be (SHOW_TURN)
    }
    "have the correct player" in {
      event = ShowPlayerStatus(SHOW_TURN, player)
      event.player should be (player)
    }
    "be able to be handled with status SHOW_TURN" in {
      event = ShowPlayerStatus(SHOW_TURN, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_PLAY_CARD" in {
      event = ShowPlayerStatus(SHOW_PLAY_CARD, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status SHOW_DOG_PLAY_CARD if arguments mismatch" in {
      event = ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status SHOW_DOG_PLAY_CARD if arguments match" in {
      event = ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, true)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status SHOW_TIE_NUMBERS if arguments mismatch" in {
      event = ShowPlayerStatus(SHOW_TIE_NUMBERS, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status SHOW_TIE_NUMBERS if arguments match" in {
      event = ShowPlayerStatus(SHOW_TIE_NUMBERS, player, 5)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_TRUMPSUIT_OPTIONS" in {
      event = ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_NOT_PLAYED" in {
      event = ShowPlayerStatus(SHOW_NOT_PLAYED, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status SHOW_WON_PLAYER_TRICK" in {
      event = ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, player)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
  }

  "The show round status event" should {
    TestUtil.disableDelay()
    val match1 = Match(List(PlayerFactory.createPlayer("Gunter", HUMAN)))
    val round = Round(trumpSuit = Hearts, matchImpl = match1, tricklist = ListBuffer(), playersin = null, firstRound = false, playersout = List(PlayerFactory.createPlayer("Foo", HUMAN)))
    var event: ShowRoundStatus = null
    "be able to be created" in {
      event = ShowRoundStatus(SHOW_START_ROUND, round)
      event should not be null
    }
    "have the correct id" in {
      event = ShowRoundStatus(SHOW_START_ROUND, round)
      event.id should be ("ShowRoundStatus")
    }
    "have the correct status" in {
      event = ShowRoundStatus(SHOW_START_ROUND, round)
      event.status should be (SHOW_START_ROUND)
    }
    "have the correct round" in {
      event = ShowRoundStatus(SHOW_START_ROUND, round)
      event.currentRound should be (round)
    }
    "be able to be handled with status SHOW_START_ROUND" in {
      event = ShowRoundStatus(SHOW_START_ROUND, round)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status WON_ROUND if arguments mismatch" in {
      event = ShowRoundStatus(WON_ROUND, round)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status WON_ROUND if arguments match" in {
      event = ShowRoundStatus(WON_ROUND, round, PlayerFactory.createPlayer("Foo", HUMAN))
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status PLAYERS_OUT" in {
      event = ShowRoundStatus(PLAYERS_OUT, round)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
  }

  "The error status event" should {
    TestUtil.disableDelay()
    var event: ShowErrorStatus = null
    "be able to be created" in {
      event = ShowErrorStatus(INVALID_NUMBER)
      event should not be null
    }
    "have the correct id" in {
      event = ShowErrorStatus(INVALID_NUMBER)
      event.id should be ("ShowErrorStatus")
    }
    "have the correct status" in {
      event = ShowErrorStatus(INVALID_NUMBER)
      event.status should be (INVALID_NUMBER)
    }
    "be able to be handled with status INVALID_NUMBER" in {
      event = ShowErrorStatus(INVALID_NUMBER)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status NOT_A_NUMBER" in {
      event = ShowErrorStatus(NOT_A_NUMBER)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status INVALID_INPUT" in {
      event = ShowErrorStatus(INVALID_INPUT)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status INVALID_NUMBER_OF_PLAYERS" in {
      event = ShowErrorStatus(INVALID_NUMBER_OF_PLAYERS)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status IDENTICAL_NAMES" in {
      event = ShowErrorStatus(IDENTICAL_NAMES)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to be handled with status INVALID_NAME_FORMAT" in {
      event = ShowErrorStatus(INVALID_NAME_FORMAT)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
    "be able to return false with status WRONG_CARD if arguments mismatch" in {
      event = ShowErrorStatus(WRONG_CARD)
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(false)
      }
    }
    "be able to return true with status WRONG_CARD if arguments match" in {
      event = ShowErrorStatus(WRONG_CARD, Card(CardValue.Queen, Suit.Hearts))
      TestUtil.cancelOut() {
        eventHandler.invoke(event) should be(true)
      }
    }
  }

  "An Event" should {
    TestUtil.disableDelay()
    "return None if it doesn't exist" in {
      val event = new SimpleEvent {
        override def id: String = "ShowEnte"
      }
      eventHandler.invoke(event) shouldBe false
    }
  }
  /*"The pick next trumpsuit event" should {
    val event = PickNextTrumpsuitEvent(Player("Foo"))
    "be able to be created" in {
      event should not be null
    }
    "have the correct id" in {
      event.id should be ("PickNextTrumpsuitEvent")
    }
    "have the correct player" in {
      event.player should be (Player("Foo"))
    }
    "be able to be handled" in {
      TestUtil.enableDebugMode()
      CardManager.resetOrder()
      TestUtil.simulateInput("1\n") {
        eventHandler.invoke(event)
      } should be (Suit.Hearts)
    }

  }*/

}
