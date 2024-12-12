package de.knockoutwhist.events.ui

import de.knockoutwhist.utils.events.SimpleEvent

enum GameState:
  case MAIN_MENU
  case INGAME
end GameState


case class GameStateUpdateEvent(gameState: GameState) extends SimpleEvent {
  override def id: String = s"GameStateUpdateEvent($gameState)"
}
