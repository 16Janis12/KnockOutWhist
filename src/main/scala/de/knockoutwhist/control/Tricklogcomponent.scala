package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.util.Try

trait Tricklogcomponent {
  /**
   * Checks if the card number is valid and if it is valid, if the player is allowed to play it.
   * @param card The card that a player wants to play.
   * @param matchImpl The current match.
   * @param round The current round.
   * @param trick The current trick.
   * @param currentIndex The index of the player queue.
   * @param player The player that wants to play a card.
   */
  def controlSuitplayed(card: Try[Card], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit

  /**
   * Checks if the card number is valid for the doglife player. 
   * The method then calls the UndoManager to play the card.
   * @param card
   * @param matchImpl
   * @param round
   * @param trick
   * @param currentIndex
   * @param player
   */
  def controlDogPlayed(card: Try[Option[Card]], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit

  /**
   * Checks the options that a player has to play a card and doesn't let the player play a wrong card.
   * @param card The current card.
   * @param round The current round.
   * @param trick The current trick.
   * @param player The player that wants to play the card.
   * @return A List of cards that are possible to be played.
   */
  def alternativeCards(card: Card, round: Round, trick: Trick, player: AbstractPlayer): List[Card]

  /**
   * Checks which player won the trick.
   * @param trick The current finished trick.
   * @param round The current round.
   * @return Returns the player that won the trick and the trick this player won.
   */
  def wonTrick(trick: Trick, round: Round): (AbstractPlayer, Trick)
}
