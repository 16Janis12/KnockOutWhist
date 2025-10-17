package de.knockoutwhist.events.old.cards

import de.knockoutwhist.cards.Hand
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

final case class RenderHandEvent(player: AbstractPlayer, hand: Hand, showNumbers: Boolean) extends SimpleEvent {
  override def id: String = "RenderHandEvent"
}
