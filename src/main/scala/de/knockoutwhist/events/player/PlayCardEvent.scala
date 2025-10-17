package de.knockoutwhist.events.player

import de.knockoutwhist.player.AbstractPlayer

case class PlayCardEvent(player: AbstractPlayer) extends PlayerEvent(player) {
  override def id: String = "PlayCardEvent"
}
