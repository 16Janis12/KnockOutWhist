package de.knockoutwhist.events.player

import de.knockoutwhist.player.AbstractPlayer

case class RequestTrumpSuitEvent(player: AbstractPlayer) extends PlayerEvent(player) {
  override def id: String = "RequestTrumpSuitEvent"
}
