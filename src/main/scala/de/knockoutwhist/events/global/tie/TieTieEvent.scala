package de.knockoutwhist.events.global.tie

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class TieTieEvent(players: List[AbstractPlayer]) extends SimpleEvent {
  override def id: String = s"TieTieEvent"
}
