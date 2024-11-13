package de.knockoutwhist.events

import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.events.SimpleEvent

enum GLOBAL_STATUS {
  case SHOW_TRICK
  case SHOW_ROUND
  case SHOW_PLAYERS_OUT
  case SHOW_PLAYERS_SAVED_BY_DOG
  case SHOW_TIE_WINNER
  case SHOW_TIE_TIE
}

enum PLAYER_STATUS {
  case SHOW_TURN
  case SHOW_HAND
  case SHOW_TIE_NUMBERS
}

enum ERROR_STATUS {
  case INVALID_NUMBER
  case NOT_A_NUMBER
}

abstract class ShowStatusEvent extends SimpleEvent {
  override def id: String = "ShowStatusEvent"
}

case class ShowGlobalStatus(status: GLOBAL_STATUS, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowGlobalStatus"
}

case class ShowPlayerStatus(status: PLAYER_STATUS, player: Player, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowPlayerStatus"
}

case class ShowErrorStatus(status: ERROR_STATUS, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowErrorStatus"
}