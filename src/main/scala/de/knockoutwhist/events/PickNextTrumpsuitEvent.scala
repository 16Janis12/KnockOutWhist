package de.knockoutwhist.events

import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.events.Event

case class PickNextTrumpsuitEvent(player: Player) extends Event {
  
}
