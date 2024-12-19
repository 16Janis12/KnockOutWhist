package de.knockoutwhist.control

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

trait Playeractrcomponent {
  /**
   * This method invokes some events to signal the player to play a valid card.
   * Then it calls handlePlayCard to get the card that the player eventually plays.
   * @param matchImpl The current match.
   * @param player The player whose turn it is to play a card.
   * @param round The current round.
   * @param trick The current trick.
   * @param currentIndex The current index of the player queue.
   */
  def playCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit

  /**
   * This method invokes some events to signal the player who is in dog mode to play a valid card.
   * Then it calls handlePlayCard to get the card that the player eventually plays or doesn't.
   * @param matchImpl The current match.
   * @param player The player whose turn it is to play a card.
   * @param round The current round.
   * @param trick The current trick
   * @param currentIndex The current index of the player queue.
   */
  def dogplayCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit

  /**
   *This method invokes some events to signal the player who won the last round to call a trumpsuit.
   *
   *
   * @param matchImpl The current match.
   * @param remaining_players The players that are still in the game.
   * @param firstRound
   * @param player The player whose turn it is to play a card.
   */
  def pickNextTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean, player: AbstractPlayer): Unit
}