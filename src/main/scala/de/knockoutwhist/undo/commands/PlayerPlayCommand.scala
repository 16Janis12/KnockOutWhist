package de.knockoutwhist.undo.commands

import de.knockoutwhist.control.MainLogic
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command

case class PlayerPlayCommand(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, currentIndex: Int = 0) extends Command {

  override def doStep(): Unit = {
    MainLogic.controlPlayer(matchImpl, round, trick, player, currentIndex)
  }

  override def undoStep(): Unit = {
    MainLogic.controlTrick(matchImpl, round, trick, currentIndex - 1)
  }
}
