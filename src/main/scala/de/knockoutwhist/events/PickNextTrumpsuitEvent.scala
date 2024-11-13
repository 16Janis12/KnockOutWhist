package de.knockoutwhist.events

import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.events.SimpleEvent

case class PickNextTrumpsuitEvent(player: Player) extends SimpleEvent {
  
}
