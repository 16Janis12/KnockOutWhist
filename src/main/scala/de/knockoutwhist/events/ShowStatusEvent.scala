package de.knockoutwhist.events

import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.utils.events.SimpleEvent

enum GLOBAL_STATUS {
  case SHOW_TRICK
  case SHOW_ROUND
  case SHOW_PLAYERS_OUT
  case SHOW_PLAYERS_SAVED_BY_DOG
  case SHOW_TIE
  case SHOW_TIE_WINNER
  case SHOW_TIE_TIE
  case SHOW_GAME_RUNNING
  case SHOW_WELCOME
  case SHOW_EXIT_GAME
  case SHOW_START_MATCH
  case SHOW_TYPE_PLAYERS
  case SHOW_MENU
}

enum PLAYER_STATUS {
  case SHOW_TURN
  case SHOW_PLAY_CARD
  case SHOW_DOG_PLAY_CARD
  case SHOW_TIE_NUMBERS
  case SHOW_TRUMPSUIT_OPTIONS
  case SHOW_NOT_PLAYED
  case SHOW_WON_PLAYER_TRICK
}
enum MATCH_STATUS {
  case SHOW_FINISHED
}
enum ROUND_STATUS {
  case SHOW_START_ROUND
  case WON_ROUND
  case PLAYERS_OUT
  case SHOW_TRICK
}

enum ERROR_STATUS {
  case INVALID_NUMBER
  case NOT_A_NUMBER
  case INVALID_INPUT
  case INVALID_NUMBER_OF_PLAYERS
  case IDENTICAL_NAMES
  case INVALID_NAME_FORMAT
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
case class ShowMatchStatus(status: MATCH_STATUS, currentMatch: Match, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowMatchStatus"
}
case class ShowRoundStatus(status: ROUND_STATUS, currentRound: Round, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowRoundStatus"
}