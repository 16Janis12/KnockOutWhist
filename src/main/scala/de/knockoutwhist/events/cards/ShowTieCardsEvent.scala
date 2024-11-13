package de.knockoutwhist.events.cards

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.events.SimpleEvent

case class ShowTieCardsEvent(card: List[(Player, Card)]) extends SimpleEvent {
  override def id: String = "ShowTieCardsEvent"
}
