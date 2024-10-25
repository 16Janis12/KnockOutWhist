package de.knockoutwhist.cards

import de.knockoutwhist.KnockOutWhist.KnockOutWhist
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.cards.Player
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.Card

import java.io.{ByteArrayOutputStream, PrintStream}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class DeckTests extends AnyWordSpec with Matchers{

  "A deck" should {
    "not be empty" in {
      CardManager.cardContainer must not be empty
    }
    "have 52 cards" in {
      CardManager.cardContainer must have size 52
    }
    "contain 13 cards of spades" in {
      CardManager.cardContainer.count(_.suit == Suit.Spades) mustBe 13
    }
    "contain 13 cards of hearts" in {
      CardManager.cardContainer.count(_.suit == Suit.Hearts) mustBe 13
    }
    "contain 13 cards of diamonds" in {
      CardManager.cardContainer.count(_.suit == Suit.Diamonds) mustBe 13
    }
    "contain 13 cards of clubs" in {
      CardManager.cardContainer.count(_.suit == Suit.Clubs) mustBe 13
    }
    "have cards in a different order after shuffling it" in {
      val originalDeck = List(CardManager.cardContainer)
      CardManager.shuffleAndReset()
      val shuffledDeck = CardManager.cardContainer
      shuffledDeck should not equal originalDeck
    }
    "provide cards" in {
      val nextCard = CardManager.nextCard()
      nextCard should not be null
    }
    "provide different cards" in {
      val nextCard = CardManager.nextCard()
      val nextCard2 = CardManager.nextCard()
      nextCard should not equal nextCard2
    }
    "supply a hand of 7 cards for the first round" in {
      val hand = CardManager.createHand()
      hand.cards should have size 7
    }
    "supply a hand of 2 cards" in {
      val hand = CardManager.createHand(2)
      hand.cards should have size 2
    }
  }
  "A card" should {
    "be displayed with correct value and Suit" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val e = "Ace of Spades"
      card.toString.equals(e) shouldBe true
    }
    "can be rendered" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val expectedResult = Array[String](
        "┌─────────┐",
        "│A        │",
        "│         │",
        "│    ♠    │",
        "│         │",
        "│        A│",
        "└─────────┘"
      )
    }
  }
  "A player" should {
    "be able to remove cards from its hand" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      val removedhand = hand.removeCard(Card(CardValue.Ace, Suit.Spades))
      removedhand.cards should have size 0
    }
    "be able to see, if he has a certain suit" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasSuit(Suit.Spades) shouldBe true
    }
    "be able to see, if he has a certain value" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasValue(CardValue.Ace) shouldBe true
    }
    "be able to see, if he has a card of Trumpsuit" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      val hand = Hand(handholder.toList)
      hand.hasTrumpSuit(Suit.Spades) shouldBe true
    }
    "be able to render his hand" in {
      val handholder = ListBuffer[Card]()
      handholder.addOne(Card(CardValue.Ace, Suit.Spades))
      handholder.addOne(Card(CardValue.Queen, Suit.Diamonds))
      val hand = Hand(handholder.toList)
      val expectedResult = List(
        "┌─────────┐ ┌─────────┐",
        "│A        │ │Q        │",
        "│         │ │         │",
        "│    ♠    │ │    ♦    │",
        "│         │ │         │",
        "│        A│ │        Q│",
        "└─────────┘ └─────────┘"
      )
      hand.renderAsString() shouldBe expectedResult
    }
  }
  "The playCard() Function" should {
    "be true for the first card played in a trick" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Spades, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player) shouldBe true
    }
    "be true if the suit matches the first card played" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Diamonds, 7, tricks_played, playerlist)
      val card = Card(CardValue.Two, Suit.Spades)
      val card2 = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe true
    }
    "be true if the card matches the trump-card" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Diamonds, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Diamonds)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe true
    }
    "be false if the card doesn't match the suit of the trump-card + first-card" in {
      val player = Player("Gunter")
      val player2 = Player("Peter")
      val playerlist = List(player, player2)
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Diamonds, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Two, Suit.Clubs)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2) shouldBe false
    }
  }
  "A trick" should {
    "be able to tell who won the trick" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Spades, 7, tricks_played, playerlist)
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
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Spades, 7, tricks_played, playerlist)
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
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Hearts, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Ten, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player)
      trick.playCard(card2, player2)
      val won = trick.wonTrick()
      won(0) shouldBe player

    }

  }
  "A player" should {
    "be able to have a hand" in {
      val card = Card(CardValue.Ace, Suit.Spades)
      val card2 = Card(CardValue.Ten, Suit.Spades)
      val card3 = Card(CardValue.Ten, Suit.Diamonds)
      val listCard = List(card, card2, card3)
      val testhand = Hand(listCard)
      val player = Player("Gunter")
      player.provideHand(testhand) shouldBe true




    }
  }
//  "Knock-out Whist" should {
//    "give me a hand" in {
//      val hand1 = CardManager.createHand()
//      val handtoString = hand1.renderAsString()
//      val card1 = (CardValue.Two, Suit.Spades)
//      val list = List(card1, )
//      //handtoString.foreach(println) shouldBe 
//    }
//  }
}
