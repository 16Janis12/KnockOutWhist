package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

trait Maincomponent {
  /**
   * Starts the game
   *
   */
  def startMatch(): Unit

  /**
   * Creates a match with the entered players by calling the UndoManager.
   * 
   * @param players The players selected by the user.
   */
  def enteredPlayers(players: List[AbstractPlayer]): Unit

  /**
   * Checks if a match is finished. 
   * If it is not finished, this method deals cards to the players that are currently in the game.
   * @param matchImpl The current match.
   */
  def controlMatch(matchImpl: Match): Unit

  /**
   * Checks if a round is finished.
   * If this is not the case, it calls the control trick method.
   * If this is the case, it finalizes the round and checks for multiple winners.
   * @param matchImpl The current match
   * @param round The current round
   */
  def controlRound(matchImpl: Match, round: Round): Unit

  /**
   * This method adds the finished round to the match and calls controlMatch.
   * @param matchImpl The current Match.
   * @param round The finished round.
   * @param winner The winner(s) of that round.
   * @param playersOut The players that lost in this round.
   */
  def endRound(matchImpl: Match, round: Round, winner: AbstractPlayer, playersOut: List[AbstractPlayer]): Unit

  /**
   * This method checks, if a trick is finished.
   * If so, it finalizes the trick and adds it to the current round.
   * If not, it calls controlPlayer to let the player play a card.
   * @param matchImpl The current match.
   * @param round The current round.
   * @param trick The current trick.
   * @param currentIndex The index of the player queue that decides which players turn it is.
   */
  def controlTrick(matchImpl: Match, round: Round, trick: Trick, currentIndex: Int = 0): Unit

  /**
   * Checks in which state the player is in (normal/ dog life) and lets the player play his card accordingly.
   * @param matchImpl The current match.
   * @param round The current round.
   * @param trick The current trick.
   * @param player The currently playing player
   * @param currentIndex The index of the player queue.
   */
  def controlPlayer(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, currentIndex: Int): Unit

  /**
   * Adds the specified card by the player to the trick.
   * @param trick The current trick.
   * @param card The card a player wants to play.
   * @param player The player that is allowed to play a card.
   */
  def playCard(trick: Trick, card: Card, player: AbstractPlayer): Trick
}
  
