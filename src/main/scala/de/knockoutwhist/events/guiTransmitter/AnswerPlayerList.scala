package de.knockoutwhist.events.guiTransmitter

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.events.{ReturnableEvent, SimpleEvent}

case class AnswerPlayerList(playerList: List[AbstractPlayer]) extends SimpleEvent {
  override def id: String = "AnswerPlayerList"
}
