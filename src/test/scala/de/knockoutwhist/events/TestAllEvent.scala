package de.knockoutwhist.events

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Two
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestNumberEvent}
import de.knockoutwhist.player.Player
import de.knockoutwhist.testutils.{TestUtil, TestUtil as shouldBe}
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.events.EventHandler
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class TestAllEvent extends AnyWordSpec with Matchers {

  private val eventHandler = new EventHandler() {}
  eventHandler.addListener(TUIMain)

  "The render hand event" should {
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
    val event = ShowTieCardsEvent(List((Player("Foo"), Card(Two,Suit.Hearts))))
    "be able to be created" in {
      event should not be null
    }
    "have the correct id" in {
      event.id should be ("ShowTieCardsEvent")
    }
    "have the correct player" in {
      event.card.head._1 should be (Player("Foo"))
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
    val event = RequestNumberEvent(6, 12)
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

  ""
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
