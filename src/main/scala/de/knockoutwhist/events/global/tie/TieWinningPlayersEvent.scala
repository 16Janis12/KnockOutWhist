package de.knockoutwhist.events.global.tie

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class TieWinningPlayersEvent(winners: List[AbstractPlayer]) extends SimpleEvent {
  override def id: String = s"TieWinningPlayersEvent"
  
  def isSingleWinner: Boolean = winners.size == 1
  
}
