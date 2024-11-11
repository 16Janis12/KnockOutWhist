package de.knockoutwhist.events

import de.knockoutwhist.cards.Hand
import de.knockoutwhist.utils.events.Event

final case class RenderHandEvent(hand: Hand, showNumbers: Boolean) extends Event {
  override def id: String = "RenderHandEvent"
}
