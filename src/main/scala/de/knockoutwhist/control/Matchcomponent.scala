package de.knockoutwhist.control

import de.knockoutwhist.rounds.Match

trait Matchcomponent {
  /**
   * Checks if the match is finished
   * @param matchImpl The current match
   * @return Returns true if it is finished, false if it is not
   */
  def isOver(matchImpl: Match): Boolean
}
