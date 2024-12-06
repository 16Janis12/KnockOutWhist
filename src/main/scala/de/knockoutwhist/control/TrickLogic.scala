package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.events.ERROR_STATUS.WRONG_CARD
import de.knockoutwhist.events.ShowErrorStatus
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Round, Trick}

object TrickLogic {
  
  private[control] def controlSuitplayed(round: Round, trick: Trick, player: AbstractPlayer): Card = {
    var card = PlayerControl.playCard(player, round, trick)
    if (trick.firstCard.isDefined) {
      val firstCard = trick.firstCard.get
      while (firstCard.suit != card.suit) {
        var hasSuit = false
        for (cardInHand <- player.currentHand().get.cards) {
          if (cardInHand.suit == firstCard.suit) {
            hasSuit = true
          }
        }
        if (!hasSuit) {
          return card
        } else {
          ControlHandler.invoke(ShowErrorStatus(WRONG_CARD, firstCard))
          card = PlayerControl.playCard(player, round, trick)
        }
      }
    }
    card
  }

  def wonTrick(trick: Trick, round: Round): (AbstractPlayer, Trick) = {
    val winningCard = {
      if (trick.cards.keys.exists(_.suit == round.trumpSuit)) {
        trick.cards.keys.filter(_.suit == round.trumpSuit).maxBy(_.cardValue.ordinal) //stream
      } else {
        trick.cards.keys.filter(_.suit == trick.firstCard.get.suit).maxBy(_.cardValue.ordinal) //stream
      }
    }
    val winningPlayer = trick.cards(winningCard)
    val finalTrick = Trick(trick.cards, winningPlayer, true)
    (winningPlayer, finalTrick)
  }
  
}
