package de.knockoutwhist.events.global.tie

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class TieEvent(players: List[AbstractPlayer]) extends SimpleEvent {
  override def id: String = s"TieEvent"
}
