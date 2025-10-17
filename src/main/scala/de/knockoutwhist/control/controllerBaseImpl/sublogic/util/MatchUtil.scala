package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.rounds.{Match, Round}

object MatchUtil {

  private def remainingRounds(matchImpl: Match, roundImpl: Round): Int = {
    //Find player with the most cards
    matchImpl.numberofcards - roundImpl.tricklist.size
  }
  
  def isRoundOver(matchImpl: Match, roundImpl: Round): Boolean = {
    //Find player with the most cards
    remainingRounds(matchImpl, roundImpl) == 0
  }

  def dogNeedsToPlay(matchImpl: Match, roundImpl: Round): Boolean = {
    remainingRounds(matchImpl, roundImpl) == 1
  }
  
}
