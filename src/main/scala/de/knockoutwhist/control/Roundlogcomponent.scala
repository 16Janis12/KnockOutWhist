package de.knockoutwhist.control

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

trait Roundlogcomponent {
  /**
   * Checks if the current round is finished.
   * @param round The current round.
   * @return True if the round is over, false if it isn't.
   */
  def isOver(round: Round): Boolean

  /**
   * Checks if the dogplayer(s) have to play their card.
   * @param round The current round.
   * @return True if they have to play their card, false if they don't.
   */
  def dogNeedsToPlay(round: Round): Boolean

  /**
   * Checks if the round is finished and then finalizes it by determining the winner of the round.
   * It then determines the players that lost and sets the right players to dog-mode.
   * @param round The current round.
   * @param matchImpl The current match.
   * @param force A debug-parameter with which rounds can get finalized even if they aren't finished.
   * @return
   */
  def finalizeRound(round: Round, matchImpl: Match, force: Boolean = false): (Match, Round, List[AbstractPlayer], List[AbstractPlayer])

  /**
   * Gets the remaining players in a round
   * @param round The current round
   * @return The remaining players as List.
   */
  def remainingPlayers(round: Round): List[AbstractPlayer]

  /**
   * Provides the exact number of cards a player needs. 
   * @param matchImpl The current match.
   * @param players The players that are still in the game
   * @return The current Match and players.
   */
  def provideCards(matchImpl: Match, players: List[AbstractPlayer]): (Match,List[AbstractPlayer])

  /**
   * Immutable broke us. If you don't know why, increase this counter: 1
   * ...
   * @param round The current round.
   * @return Returns the current round.
   */
  def smashResults(round: Round): Round
}
