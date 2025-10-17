package de.knockoutwhist.events.player

import de.knockoutwhist.player.AbstractPlayer

case class RequestTieChoiceEvent(player: AbstractPlayer, maxNumber: Int) extends PlayerEvent(player) {
  override def id: String = "RequestTieChoiceEvent"
}
