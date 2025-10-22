package de.knockoutwhist.events.global

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.utils.events.SimpleEvent

case class CardPlayedEvent(player: AbstractPlayer, trick: Trick) extends SimpleEvent {
  override def id: String = s"CardPlayedEvent"
}
