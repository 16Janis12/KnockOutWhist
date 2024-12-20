package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.control.{ControlHandler, Tricklogcomponent}
import de.knockoutwhist.control.controllerBaseImpl.PlayerControl
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_INPUT, INVALID_NUMBER, WRONG_CARD}
import de.knockoutwhist.events.ShowErrorStatus
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.{PlayerPlayCommand, PlayerPlayDogCommand}

import scala.util.Try

object TrickLogic extends Tricklogcomponent {
  
  def controlSuitplayed(card: Try[Card], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit = {
    if (card.isFailure) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER))
      KnockOutWhist.config.playeractrcomponent.playCard(matchImpl, player, round, trick, currentIndex)
      return
    }
    val realCard = card.get
    if (trick.firstCard.isDefined) {
      val firstCard = trick.firstCard.get
      if (firstCard.suit != realCard.suit) {
        var hasSuit = false
        for (cardInHand <- player.currentHand().get.cards) {
          if (cardInHand.suit == firstCard.suit) {
            hasSuit = true
          }
        }
        if (hasSuit) {
          ControlHandler.invoke(ShowErrorStatus(WRONG_CARD, firstCard))
          KnockOutWhist.config.playeractrcomponent.playCard(matchImpl, player, round, trick, currentIndex)
          return
        }
      }
    }
    UndoManager.doStep(PlayerPlayCommand(matchImpl, round, trick, player, realCard, currentIndex))
  }
  
  def controlDogPlayed(card: Try[Option[Card]], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit = {
    if (card.isFailure) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_INPUT))
      KnockOutWhist.config.playeractrcomponent.dogplayCard(matchImpl, player, round, trick, currentIndex)
      return
    }
    UndoManager.doStep(PlayerPlayDogCommand(matchImpl, round, trick, player, card.get, currentIndex))
  }
  
  def alternativeCards(card: Card, round: Round, trick: Trick, player: AbstractPlayer): List[Card] = {
    if (trick.firstCard.isDefined) {
      val firstCard = trick.firstCard.get
      if (firstCard.suit != card.suit) {
        val alternatives: List[Card] = for cardInHand <- player.currentHand().get.cards
          if cardInHand.suit == firstCard.suit
            yield cardInHand
        if(round.trumpSuit == card.suit && alternatives.isEmpty) {
          return Nil
        }
        if (alternatives.nonEmpty) {
          return alternatives
        }
      }
    }
    Nil
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
