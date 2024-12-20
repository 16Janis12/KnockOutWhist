package de.knockoutwhist.control

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

trait Roundlogcomponent {
  
  def isOver(round: Round): Boolean
  
  def dogNeedsToPlay(round: Round): Boolean
  
  def finalizeRound(round: Round, matchImpl: Match, force: Boolean = false): (Match, Round, List[AbstractPlayer], List[AbstractPlayer])
  
  def remainingPlayers(round: Round): List[AbstractPlayer]
  
  def provideCards(matchImpl: Match, players: List[AbstractPlayer]): (Match,List[AbstractPlayer])
  
  def smashResults(round: Round): Round
}
