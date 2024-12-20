package de.knockoutwhist.undo.commands

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, ControlThread}
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
      KnockOutWhist.config.maincomponent.controlMatch(matchImpl)
    }
  }

  override def undoStep(): Unit = {
    KnockOutWhist.config.maincomponent.startMatch()
  }
}
