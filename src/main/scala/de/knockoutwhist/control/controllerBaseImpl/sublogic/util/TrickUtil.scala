package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.CustomPlayerQueue

object TrickUtil {
  
  def isOver(matchImpl: Match, queue: CustomPlayerQueue[AbstractPlayer]): Boolean = {
    queue.playersSinceLastReset() >= matchImpl.playersIn.length
  }

  private def winningPlayer(trick: Trick, round: Round): AbstractPlayer = {
    val winningCard = {
      if (trick.cards.keys.exists(_.suit == round.trumpSuit)) {
        trick.cards.keys.filter(_.suit == round.trumpSuit).maxBy(_.cardValue.ordinal) //stream
      } else {
        trick.cards.keys.filter(_.suit == trick.firstCard.get.suit).maxBy(_.cardValue.ordinal) //stream
      }
    }
    val winningPlayer = trick.cards(winningCard)
    winningPlayer
  }
  
  def finishTrick(trick: Trick, round: Round): TrickResult = {
    val winner = winningPlayer(trick, round)
    TrickResult(winner)
  }

}

case class TrickResult(winner: AbstractPlayer)
