package de.knockoutwhist.events.global

import de.knockoutwhist.control.GameState
import de.knockoutwhist.utils.events.SimpleEvent

case class GameStateChangeEvent(oldState: GameState, newState: GameState) extends SimpleEvent {
  override def id: String = s"GameStateChangeEvent(from=$oldState,to=$newState)"
}
