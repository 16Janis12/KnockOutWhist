package de.knockoutwhist.events.cards

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.SimpleEvent

case class ShowTieCardsEvent(card: List[(AbstractPlayer, Card)]) extends SimpleEvent {
  override def id: String = "ShowTieCardsEvent"
}
