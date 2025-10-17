package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Round, Trick}

object PlayerUtil {
  
  def canPlayCard(card: Card, round: Round, trick: Trick, player: AbstractPlayer): Boolean = {
    if (trick.firstCard.isDefined) {
      val firstCard = trick.firstCard.get
      if (firstCard.suit != card.suit) {
        val alternatives: List[Card] = for cardInHand <- player.currentHand().get.cards
                                           if cardInHand.suit == firstCard.suit
        yield cardInHand
        if (round.trumpSuit == card.suit && alternatives.isEmpty) {
          return true
        }
        if (alternatives.nonEmpty) {
          return false
        }
      }
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

}
