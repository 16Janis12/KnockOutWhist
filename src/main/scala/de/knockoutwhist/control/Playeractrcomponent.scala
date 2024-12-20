package de.knockoutwhist.control

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

trait Playeractrcomponent {
  
  def playCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit

  
  def dogplayCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit

  
  def pickNextTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean, player: AbstractPlayer): Unit
}