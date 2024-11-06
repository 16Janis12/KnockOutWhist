package de.knockoutwhist.rounds

import de.knockoutwhist.cards.{Card, CardValue, Suit}
import de.knockoutwhist.player.Player
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.immutable.HashMap

class TrickTests extends AnyWordSpec with Matchers {

  "A trick" should {
    "be able to return the first card after it was played" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Clubs)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.get_first_card().isEmpty shouldBe false
      trick.get_first_card().get shouldBe card
    }
    "be able to return no first card when none was played" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val trick = new Trick(round)
      trick.get_first_card().isEmpty shouldBe true
    }
    "be able to tell who won the trick" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val round = new Round(Suit.Spades, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)

      trick.playCard(card, player)

      val won = trick.wonTrick()

      won(0) shouldBe player
      won(1).cards should equal(trick.cards)
      won(1).winner should equal(player)
      won(1).winner should equal(won(0))
    }
    "throw a IllegalStateException if it is already finished" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val round = new Round(Suit.Spades, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player)

      val won = trick.wonTrick()
      assertThrows[IllegalStateException] { //If exception is thrown, assertThrows returns succeeded
        won(1).playCard(card, player)
      }
    }
    "filter the cards by suit correctly if no trump was played" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Hearts, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Ten, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2)
      val won = trick.wonTrick()
      won(0) shouldBe player

    }
    "return true for the first card played in a trick" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val round = new Round(Suit.Spades, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player) shouldBe true
    }
    "return true if the suit matches the first card played" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Two, Suit.Spades)
      val card2 = Card(CardValue.Ace, Suit.Spades)
      val trick = round.create_trick()
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe true
    }
    "return true if the card matches the trump-card" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Diamonds)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe true
    }
    "return false if the card doesn't match the suit of the trump-card + first-card" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Clubs)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe false
    }
    "have a working to string" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val round = new Round(Suit.Diamonds, Match(playerlist), playerlist, false)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Clubs)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2)
      trick.toString() shouldBe s"${round}, ${HashMap[Card, Player](card2 -> player2, card -> player)}, ${null}, ${false}"
    }
  }

}
