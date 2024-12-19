package de.knockoutwhist.undo.commands

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.Command

case class TrumpSuitSelectedCommand(matchImpl: Match, suit: Suit, remaining_players: List[AbstractPlayer], firstRound: Boolean, decided: AbstractPlayer) extends Command {

  override def doStep(): Unit = {
    val newMatchImpl = matchImpl.setNumberOfCards(matchImpl.numberofcards - 1)
    val round = new Round(suit, remaining_players, firstRound)
    round.playerQueue.resetAndSetStart(decided)
    ControlHandler.maincomponent.controlRound(newMatchImpl, round)
  }

  override def undoStep(): Unit = {
    ControlHandler.playerlogcomponent.trumpsuitStep(matchImpl, remaining_players)
  }
}
