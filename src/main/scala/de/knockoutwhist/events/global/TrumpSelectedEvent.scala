package de.knockoutwhist.events.global

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class TrumpSelectedEvent(suit: Suit) extends SimpleEvent{
  
  override def id: String = "TrumpSelectEvent"

}
