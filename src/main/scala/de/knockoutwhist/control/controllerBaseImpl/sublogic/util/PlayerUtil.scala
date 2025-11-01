package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Round, Trick}

object PlayerUtil {
  
  def canPlayCard(card: Card, round: Round, trick: Trick, player: AbstractPlayer): Boolean = {
    if (trick.firstCard.isEmpty)
      return true
    val alternatives = alternativeCards(card, round, trick, player)
    if (alternatives.nonEmpty) {
      return false
    }
    true
  }

  def alternativeCards(card: Card, round: Round, trick: Trick, player: AbstractPlayer): List[Card] = {
    if (trick.firstCard.isDefined) {
      val firstCard = trick.firstCard.get
      if (firstCard.suit != card.suit) {
        val alternatives: List[Card] = for cardInHand <- player.currentHand().get.cards
                                           if cardInHand.suit == firstCard.suit
        yield cardInHand
        if (round.trumpSuit == card.suit && alternatives.isEmpty) {
          return Nil
        }
        if (alternatives.nonEmpty) {
          return alternatives
        }
      }
    }
    Nil
  }

  def playableCards(round: Round, trick: Trick, player: AbstractPlayer): List[Card] = {
    val handOption = player.currentHand()
    if (handOption.isEmpty) {
      throw new IllegalStateException("You have no cards!")
    }
    val hand = handOption.get
    if (trick.firstCard.isEmpty) {
      return hand.cards
    }
    val playableCards: List[Card] = for cardInHand <- hand.cards
                                        if canPlayCard(cardInHand, round, trick, player)
    yield cardInHand
    playableCards
  }

}
