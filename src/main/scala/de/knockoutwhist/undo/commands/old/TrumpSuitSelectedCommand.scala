package de.knockoutwhist.undo.commands.old

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.Command

case class TrumpSuitSelectedCommand(matchImpl: Match, suit: Suit, remaining_players: List[AbstractPlayer], firstRound: Boolean, decided: AbstractPlayer) extends Command {

  override def doStep(): Unit = {
    val newMatchImpl = matchImpl.setNumberOfCards(matchImpl.numberofcards - 1)
    val round = new Round(suit, remaining_players, firstRound)
    round.playerQueue.resetAndSetStart(decided)
    KnockOutWhist.config.maincomponent.controlRound(newMatchImpl, round)
  }

  override def undoStep(): Unit = {
    KnockOutWhist.config.playerlogcomponent.trumpsuitStep(matchImpl, remaining_players)
  }
}
