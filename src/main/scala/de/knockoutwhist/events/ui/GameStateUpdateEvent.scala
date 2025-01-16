package de.knockoutwhist.events.ui

import de.knockoutwhist.utils.events.SimpleEvent

enum GameState:
  case NO_SET
  case MAIN_MENU
  case PLAYERS
  case INGAME
  case TRUMPSUIT
end GameState


case class GameStateUpdateEvent(gameState: GameState) extends SimpleEvent {
  override def id: String = s"GameStateUpdateEvent($gameState)"
}
