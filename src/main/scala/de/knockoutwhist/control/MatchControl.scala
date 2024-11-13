package de.knockoutwhist.control

import de.knockoutwhist.rounds.{Match, Round, Trick}

trait MatchControl {
  
  def initial(): Boolean
  def start(): Unit
  
  /**
   * Start the next round
   * @return the next round or null if the match is over
   */
  def nextRound(matchImpl: Match): Round

  /**
   * Start the next trick
   * @return the last trick or null if the round is over
   */
  def nextTrick(roundImpl: Round): Trick
  

}
