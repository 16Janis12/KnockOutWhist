package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.rounds.{Match, Round}

object MatchUtil {

  private def remainingRounds(matchImpl: Match, roundImpl: Round): Int = {
    // If no player has any cards left, there are no rounds remaining
    if !matchImpl.playersIn
      .map(player => player.currentHand()).exists(hand => hand.isDefined && hand.get.cards.nonEmpty) then return 0
    // Otherwise, calculate remaining rounds based on number of cards and tricks played
    matchImpl.numberofcards - roundImpl.tricklist.size
  }
  
  def isRoundOver(matchImpl: Match, roundImpl: Round): Boolean = {
    remainingRounds(matchImpl, roundImpl) == 0
  }

  def dogNeedsToPlay(matchImpl: Match, roundImpl: Round): Boolean = {
    remainingRounds(matchImpl, roundImpl) <= 1
  }
  
}
