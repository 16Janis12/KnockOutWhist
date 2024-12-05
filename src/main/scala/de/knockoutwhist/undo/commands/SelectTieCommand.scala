package de.knockoutwhist.undo.commands

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.PlayerLogic
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.Command

import scala.collection.immutable

case class SelectTieCommand(winner: List[AbstractPlayer],matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0) extends Command {

  override def doStep(): Unit = {
    PlayerLogic.selectTie(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex)
  }

  override def undoStep(): Unit = {
    if (currentIndex == 1) {
      PlayerLogic.preSelect(winner, matchImpl, round, playersout)
    }
    else {
      PlayerLogic.selectTie(winner, matchImpl, round, playersout, cut, currentStep, remaining, currentIndex-1)
    }
  }
}
