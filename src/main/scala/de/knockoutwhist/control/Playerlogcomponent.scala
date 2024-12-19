package de.knockoutwhist.control

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

import scala.collection.immutable
import scala.util.Try

trait Playerlogcomponent {
  /**
   * Checks, if the round played is the first round or not.
   * If it is, it selects a random trump suit.
   * If not it lets a player decide.
   * @param matchImpl The current match.
   * @param remaining_players The players that are still in the game.
   */
  def trumpsuitStep(matchImpl: Match, remaining_players: List[AbstractPlayer]): Unit

  /**
   * Checks if the selection worked. It then calls the UndoManager to set the trumpsuit.
   * @param matchImpl The current match.
   * @param suit The selected suit.
   * @param remaining_players The players that are still in the game.
   * @param firstRound If it is the first round.
   * @param decided The player that selected the trumpsuit.
   */
  def trumpSuitSelected(matchImpl: Match, suit: Try[Suit], remaining_players: List[AbstractPlayer], firstRound: Boolean, decided: AbstractPlayer): Unit

  /**
   * Shuffles the cards for the cut and calls selecttie to manage the cut.
   * @param winners The players that won the previous round and are now cutting to determine the trumpsuit caller.
   * @param matchImpl The current match.
   * @param round The current round.
   * @param playersout The players that are out of this game.
   */
  def preSelect(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer]): Unit

  /**
   * Checks if the cut is finished.
   * If it is, the method calls evaluatewinnertie to determine the winner of the cut.
   * If not, the method lets the next player pick a card.
   * @param winners The players that won the previous round and are now cutting to determine the trumpsuit caller.
   * @param matchImpl The current match.
   * @param round The current round.
   * @param playersout The players that are out of the game
   * @param cut A HashMap that stores the selected cut cards.
   * @param currentStep
   * @param remaining The remaining cards in the deck for the cut.
   * @param currentIndex The index of the player that is allowed to pick a card.
   */
  def selectTie(winners: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit

  /**
   *
   * @param winner
   * @param matchImpl
   * @param round
   * @param playersout
   * @param cut
   * @param value
   * @param currentStep
   * @param remaining
   * @param currentIndex
   */
  def selectedTie(winner: List[AbstractPlayer],matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], value: Try[Int], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit

  /**
   *
   * @param matchImpl
   * @param round
   * @param playersout
   * @param cut
   */
  def evaluateTieWinner(matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card]): Unit
}
