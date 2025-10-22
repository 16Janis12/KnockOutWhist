package de.knockoutwhist.events.player

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

import java.util.UUID

abstract class PlayerEvent(player: AbstractPlayer) extends SimpleEvent {

  def playerId: UUID = player.id

}
