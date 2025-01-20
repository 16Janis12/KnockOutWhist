package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, Matchcomponent}
import de.knockoutwhist.rounds.Match

object MatchLogic extends Matchcomponent {
  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      KnockOutWhist.config.roundlogcomponent.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }
}
