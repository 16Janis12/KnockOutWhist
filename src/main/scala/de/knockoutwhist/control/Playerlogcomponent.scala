package de.knockoutwhist.control

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

import scala.collection.immutable
import scala.util.Try

trait Playerlogcomponent {
  
  def trumpsuitStep(matchImpl: Match, remaining_players: List[AbstractPlayer]): Unit
  
  def trumpSuitSelected(matchImpl: Match, suit: Try[Suit], remaining_players: List[AbstractPlayer], firstRound: Boolean, decided: AbstractPlayer): Unit
  
  def preSelect(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer]): Unit
  
  def selectTie(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit
  
  def selectedTie(winner: List[AbstractPlayer],matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], value: Try[Int], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit
  
  def evaluateTieWinner(matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card]): Unit
}
