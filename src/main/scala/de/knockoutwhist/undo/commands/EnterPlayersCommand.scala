package de.knockoutwhist.undo.commands

import de.knockoutwhist.control.{ControlHandler, ControlThread, MainLogic}
import de.knockoutwhist.events.ui.GameState.INGAME
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.undo.Command

case class EnterPlayersCommand(players: List[AbstractPlayer]) extends Command {

  override def doStep(): Unit = {
    val matchImpl = Match(players)
    ControlThread.runLater {
      ControlHandler.invoke(GameStateUpdateEvent(INGAME))
      MainLogic.controlMatch(matchImpl)
    }
  }

  override def undoStep(): Unit = {
    MainLogic.startMatch()
  }
}
