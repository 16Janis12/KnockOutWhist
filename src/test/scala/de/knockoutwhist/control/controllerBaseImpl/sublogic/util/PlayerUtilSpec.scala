package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.player.StubPlayer

class PlayerUtilSpec extends AnyWordSpec with Matchers {

  "PlayerUtil" should {

    "allow play when no first card has been set" in {
      // Arrange
      val round = Round(Suit.Spades, firstRound = true)
      val trick = Trick()
      val player = new StubPlayer("p1")
      val card = Card(CardValue.Ace, Suit.Hearts)
      player.provideHand(Hand(List(card)))

      // Act
      val can = PlayerUtil.canPlayCard(card, round, trick, player)

      // Assert
      can shouldBe true
    }

    "disallow play when must follow suit and alternatives exist" in {
      // Arrange
      val round = Round(Suit.Clubs, firstRound = false)
      val lead = Card(CardValue.Two, Suit.Hearts)
      val trick = Trick(firstCard = Some(lead))
      val player = new StubPlayer("p1")
      val toPlay = Card(CardValue.Ace, Suit.Spades) // different suit than lead
      val alt1 = Card(CardValue.Three, Suit.Hearts)
      val alt2 = Card(CardValue.Four, Suit.Hearts)
      player.provideHand(Hand(List(toPlay, alt1, alt2)))

      // Act
      val alts = PlayerUtil.alternativeCards(toPlay, round, trick, player)
      val can = PlayerUtil.canPlayCard(toPlay, round, trick, player)

      // Assert
      alts should contain allOf (alt1, alt2)
      can shouldBe false
    }

    "allow play when card is trump and no alternatives of lead suit exist" in {
      // Arrange
      val round = Round(Suit.Spades, firstRound = false)
      val lead = Card(CardValue.Two, Suit.Hearts)
      val trick = Trick(firstCard = Some(lead))
      val player = new StubPlayer("p1")
      val trumpCard = Card(CardValue.Ace, Suit.Spades) // trump
      val offSuit = Card(CardValue.Five, Suit.Diamonds)
      player.provideHand(Hand(List(trumpCard, offSuit)))

      // Act
      val alts = PlayerUtil.alternativeCards(trumpCard, round, trick, player)
      val can = PlayerUtil.canPlayCard(trumpCard, round, trick, player)

      // Assert
      alts shouldBe Nil
      can shouldBe true
    }

    "allow play when lead suit equals played card suit (no restriction)" in {
      // Arrange
      val round = Round(Suit.Clubs, firstRound = false)
      val lead = Card(CardValue.Seven, Suit.Diamonds)
      val trick = Trick(firstCard = Some(lead))
      val player = new StubPlayer("p1")
      val toPlay = Card(CardValue.Queen, Suit.Diamonds)
      val other = Card(CardValue.Four, Suit.Hearts)
      player.provideHand(Hand(List(toPlay, other)))

      // Act
      val alts = PlayerUtil.alternativeCards(toPlay, round, trick, player)
      val can = PlayerUtil.canPlayCard(toPlay, round, trick, player)

      // Assert
      alts shouldBe Nil
      can shouldBe true
    }
  }
}
