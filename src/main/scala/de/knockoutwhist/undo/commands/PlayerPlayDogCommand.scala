package de.knockoutwhist.undo.commands

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_NOT_PLAYED
import de.knockoutwhist.events.ShowPlayerStatus
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command

case class PlayerPlayDogCommand(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, rightCard: Option[Card], currentIndex: Int = 0) extends Command {

  override def doStep(): Unit = {
    if (rightCard.isEmpty) {
      ControlHandler.invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
      KnockOutWhist.config.maincomponent.controlTrick(matchImpl, round, trick, currentIndex + 1)
    } else {
      val newPlayer = player.removeCard(rightCard.get)
      val newTrick = KnockOutWhist.config.maincomponent.playCard(trick, rightCard.get, newPlayer)
      val newRound = round.updatePlayersIn(round.playersin.updated(round.playersin.indexOf(player), newPlayer))
      val newMatch = matchImpl.updatePlayers(matchImpl.totalplayers.updated(matchImpl.totalplayers.indexOf(player), newPlayer))
      KnockOutWhist.config.maincomponent.controlTrick(newMatch, newRound, newTrick, currentIndex + 1)
    }
  }

  override def undoStep(): Unit = {
    KnockOutWhist.config.maincomponent.controlPlayer(matchImpl, round, trick, player, currentIndex)
  }
}
