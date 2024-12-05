package de.knockoutwhist.undo.commands

import de.knockoutwhist.control.MainLogic
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.undo.Command

case class EnterPlayersCommand(players: List[AbstractPlayer]) extends Command {

  override def doStep(): Unit = {
    val matchImpl = Match(players)
    MainLogic.controlMatch(matchImpl)
  }

  override def undoStep(): Unit = {
    MainLogic.startMatch()
  }
}
