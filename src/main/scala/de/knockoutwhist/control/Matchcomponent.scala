package de.knockoutwhist.control

import de.knockoutwhist.rounds.Match

trait Matchcomponent {
  
  def isOver(matchImpl: Match): Boolean
}
