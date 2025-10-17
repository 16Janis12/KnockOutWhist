package de.knockoutwhist.undo.commands

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, ControlThread}
import de.knockoutwhist.events.old.GLOBAL_STATUS.TECHNICAL_MATCH_STARTED
import de.knockoutwhist.events.old.ShowGlobalStatus
import de.knockoutwhist.events.old.ui.GameState.INGAME
import de.knockoutwhist.events.old.ui.GameStateUpdateEvent
import de.knockoutwhist.persistence.MethodEntryPoint.EnterPlayers
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Match
import de.knockoutwhist.undo.Command

case class EnterPlayersCommand(players: List[AbstractPlayer]) extends Command {

  override def doStep(): Unit = {
    val matchImpl = Match(totalplayers = players, cardManager = KnockOutWhist.config.cardManager)
    KnockOutWhist.config.persistenceManager.updateMatch(matchImpl)
    KnockOutWhist.config.persistenceManager.updateMethodEntryPoint(EnterPlayers)
    ControlThread.runLater {
      ControlHandler.invoke(GameStateUpdateEvent(INGAME))
      ControlHandler.invoke(ShowGlobalStatus(TECHNICAL_MATCH_STARTED, matchImpl))
      KnockOutWhist.config.maincomponent.controlMatch(matchImpl)
    }
  }

  override def undoStep(): Unit = {
    KnockOutWhist.config.maincomponent.startMatch()
  }
}
