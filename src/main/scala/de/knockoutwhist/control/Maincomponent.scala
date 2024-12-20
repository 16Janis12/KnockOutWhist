package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

trait Maincomponent {
  
  def startMatch(): Unit

  
  def enteredPlayers(players: List[AbstractPlayer]): Unit

  
  def controlMatch(matchImpl: Match): Unit

  
  def controlRound(matchImpl: Match, round: Round): Unit

  
  def endRound(matchImpl: Match, round: Round, winner: AbstractPlayer, playersOut: List[AbstractPlayer]): Unit

  
  def controlTrick(matchImpl: Match, round: Round, trick: Trick, currentIndex: Int = 0): Unit

  
  def controlPlayer(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, currentIndex: Int): Unit
  
  def playCard(trick: Trick, card: Card, player: AbstractPlayer): Trick
}
  
