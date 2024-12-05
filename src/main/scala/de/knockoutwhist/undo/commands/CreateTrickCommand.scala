package de.knockoutwhist.undo.commands

import de.knockoutwhist.control.MainLogic
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command

case class CreateTrickCommand(matchImpl: Match, round: Round) extends Command {

  override def doStep(): Unit = {
    val trick = Trick()
    MainLogic.controlTrick(matchImpl, round, trick)
  }

  override def undoStep(): Unit = {
    MainLogic.controlRound(matchImpl, round)
  }
}
