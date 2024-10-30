package de.knockoutwhist.control

import de.knockoutwhist.cards.{Card, Player}
import de.knockoutwhist.rounds.Round

trait PlayerControl {

  def playCard(player: Player): Card
  def determineWinnerTie(players: List[Player], tieMessage: Boolean = true): Player
  def showCards(player: Player): Boolean
  def showWon(player: Player, round: Round): Int
  
}
