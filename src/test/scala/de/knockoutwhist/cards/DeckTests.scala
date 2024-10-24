package de.knockoutwhist.cards

import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.cards.Player

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
  "A trick" should {
    "be able to place a card in the HashMap" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Spades, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      trick.playCard(card, player) shouldBe true
    }
    "be able to tell who won the trick" in {
      val playerlist = List(Player("Gunter"))
      val player = Player("Gunter")
      val tricks_played: ListBuffer[Trick] = ListBuffer.empty[Trick]
      val round = Round(Suit.Spades, 7, tricks_played, playerlist)
      val card = Card(CardValue.Ace, Suit.Spades)
      val trick = new Trick(round)
      // val hashMap: mutable.HashMap[Card, Player] = mutable.HashMap.empty[Card, Player]
      // hashMap += (card, player)

      trick.playCard(card, player)
      trick.wonTrick() shouldBe (player,trick)

    }
  }

}
