package de.knockoutwhist.control

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.Round

trait PlayerControl {

  def playCard(player: Player): Card
  def dogplayCard(player: Player): Option[Card]
  def determineWinnerTie(players: List[Player]): Player
  def pickNextTrumpsuit(player: Player): Suit
  def showCards(player: Player): Boolean
  def showWon(player: Player, round: Round): Boolean
  
}
