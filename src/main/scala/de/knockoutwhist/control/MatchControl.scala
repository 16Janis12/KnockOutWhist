package de.knockoutwhist.control

import de.knockoutwhist.cards.Player
import de.knockoutwhist.rounds.{Round, Trick}

trait MatchControl {
  
  def initial(): Boolean
  def start(): Unit
  def playerControl: PlayerControl
  
  /**
   * Start the next round
   * @return the last round or null if it is the first
   */
  def nextRound(): Round

  /**
   * Start the next trick
   * @return the last trick or null if it is the first
   */
  def nextTrick(): Trick
  

}
