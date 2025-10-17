package de.knockoutwhist.events.global

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class RoundEndEvent(winner: AbstractPlayer) extends SimpleEvent {
  override def id: String = s"RoundEndEvent()"
}
