package de.knockoutwhist.events.global.tie

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class TieShowPlayerCardsEvent() extends SimpleEvent {
  override def id: String = s"TieShowPlayerCardsEvent"
}
