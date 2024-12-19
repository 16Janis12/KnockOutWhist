package de.knockoutwhist.undo.commands

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.Command

case class PlayerPlayCommand(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, rightCard: Card, currentIndex: Int = 0) extends Command {

  override def doStep(): Unit = {
    val newPlayer = player.removeCard(rightCard)
    val newTrick = ControlHandler.maincomponent.playCard(trick, rightCard, newPlayer)
    val newRound = round.updatePlayersIn(round.playersin.updated(round.playersin.indexOf(player), newPlayer))
    val newMatch = matchImpl.updatePlayers(matchImpl.totalplayers.updated(matchImpl.totalplayers.indexOf(player), newPlayer))
    ControlHandler.maincomponent.controlTrick(newMatch, newRound, newTrick, currentIndex+1)
  }

  override def undoStep(): Unit = {
    ControlHandler.maincomponent.controlPlayer(matchImpl, round, trick, player, currentIndex)
  }
}
