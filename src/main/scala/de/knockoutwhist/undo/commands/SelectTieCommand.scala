package de.knockoutwhist.undo.commands

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.Command

import scala.collection.immutable

case class SelectTieCommand(winner: List[AbstractPlayer],matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], value: Int, selCard: Card, currentStep: Int, remaining: Int, currentIndex: Int = 0) extends Command {

  override def doStep(): Unit = {
    val player = winner(currentIndex)
    val newCut = cut + (player -> selCard)
    val newCurrentStep = currentStep + value
    val newRemaining = remaining - (value - 1)
    KnockOutWhist.config.playerlogcomponent.selectTie(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex + 1)
  }

  override def undoStep(): Unit = {
    if (currentIndex == 0) {
      KnockOutWhist.config.playerlogcomponent.preSelect(winner, matchImpl, round, playersout)
    }
    else {
      KnockOutWhist.config.playerlogcomponent.selectTie(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex)
    }
  }
}
