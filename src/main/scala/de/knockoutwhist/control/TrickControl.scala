package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.MatchControl.playerQueue
import de.knockoutwhist.events.ERROR_STATUS.WRONG_CARD
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowPlayerStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Round, Trick}

import de.knockoutwhist.utils.Implicits._

object TrickControl {

  def playCard(trick: Trick, round: Round, card: Card, player: AbstractPlayer): (Trick, Boolean) = {
    if (trick.finished) {
      throw new IllegalStateException("This trick is already finished")
    } else {
      if (trick.firstCard.isEmpty) {
        val tr = trick.setfirstcard(card)
        (tr.addCard(card, player), true)
      } else if ((card.suit == trick.firstCard.getOrElse(card).suit) || (card.suit == round.trumpSuit)) { // Wert aus Option extrahieren
        (trick.addCard(card, player), true)
      } else {
        (trick.addCard(card, player), false)
      }
    }
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
    val finalTrick = Trick(round, trick.cards, winningPlayer, true)
    round.tricklist += finalTrick
    (winningPlayer, finalTrick)
  }

  def createtrick(round: Round): Trick = {
    val trick = new Trick(round)
    round.setcurrenttrick(trick)
    trick
  }

  def controlTrick(trickProv: Option[Trick] = None,round: Round): Trick = {
    var trick = trickProv.isEmpty ? createtrick(round) |: trickProv.get
    for (player <- playerQueue) {
      ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
      if (!player.doglife) {
        val rightCard = controlSuitplayed(trick, player)
        player.removeCard(rightCard)
        trick = TrickControl.playCard(trick, round, rightCard, player)._1
      } else if (player.currentHand().exists(_.cards.nonEmpty)) {
        val card = PlayerControl.dogplayCard(player, round, trick)
        if (card.isEmpty) {
          ControlHandler.invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
        } else {
          player.removeCard(card.get)
          trick = TrickControl.playCard(trick, round, card.get, player)._1
        }
      }
      trick.remainingPlayers -= 1
    }
    val (winner, finalTrick) = TrickControl.wonTrick(trick, round)
    ControlHandler.invoke(ShowCurrentTrickEvent(round, finalTrick))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, winner))
    playerQueue.resetAndSetStart(winner)
    ControlHandler.invoke(DelayEvent(3000L))
    finalTrick
  }

  def nextTrick(roundImpl: Round): Trick = {
    if (RoundControl.isOver(roundImpl)) {
      return null
    }
    createtrick(roundImpl)
  }

  private[control] def controlSuitplayed(trick: Trick, player: AbstractPlayer): Card = {
    var card = PlayerControl.playCard(player, trick)
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
          card = PlayerControl.playCard(player, trick)
        }
      }
    }
    card
  }
}
