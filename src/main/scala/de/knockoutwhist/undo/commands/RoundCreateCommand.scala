package de.knockoutwhist.undo.commands

import de.knockoutwhist.control.{MainLogic, PlayerLogic, RoundLogic}
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.undo.Command
import de.knockoutwhist.utils.Implicits.*

case class RoundCreateCommand(matchImpl: Match) extends Command {

  override def doStep(): Unit = {
    val remainingPlayer = matchImpl.roundlist.isEmpty ? matchImpl.totalplayers |: RoundLogic.remainingPlayers(matchImpl.roundlist.last)
    val newMatch = RoundLogic.provideCards(matchImpl, remainingPlayer)
    PlayerLogic.trumpsuitStep(newMatch._1, newMatch._2)
  }

  override def undoStep(): Unit = {
    MainLogic.controlMatch(matchImpl)
  }
}
