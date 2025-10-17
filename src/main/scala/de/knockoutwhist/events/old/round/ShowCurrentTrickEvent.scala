package de.knockoutwhist.events.old.round

import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.utils.events.SimpleEvent

case class ShowCurrentTrickEvent(round: Round, trick: Trick) extends SimpleEvent {
  override def id: String = "ShowCurrentTrickEvent"
}
