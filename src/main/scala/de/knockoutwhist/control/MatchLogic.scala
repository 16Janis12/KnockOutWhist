package de.knockoutwhist.control

import de.knockoutwhist.rounds.Match

object MatchLogic {
  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      RoundLogic.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }
}
