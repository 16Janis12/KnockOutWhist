package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.control.{ControlHandler, Matchcomponent}
import de.knockoutwhist.rounds.Match

object MatchLogic extends Matchcomponent {
  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      ControlHandler.roundlogcomponent.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }
}
