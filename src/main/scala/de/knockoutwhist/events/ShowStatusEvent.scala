package de.knockoutwhist.events

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.utils.events.SimpleEvent

enum GLOBAL_STATUS {
  case SHOW_TIE
  case SHOW_TIE_WINNER
  case SHOW_TIE_TIE
  
  case SHOW_START_MATCH
  case SHOW_TYPE_PLAYERS
  case SHOW_FINISHED_MATCH
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
enum ROUND_STATUS {
  case SHOW_START_ROUND
  case WON_ROUND
  case PLAYERS_OUT
}

enum ERROR_STATUS {
  case INVALID_NUMBER
  case NOT_A_NUMBER
  case INVALID_INPUT
  case INVALID_NUMBER_OF_PLAYERS
  case IDENTICAL_NAMES
  case INVALID_NAME_FORMAT
  case WRONG_CARD
}

abstract class ShowStatusEvent extends SimpleEvent

case class ShowGlobalStatus(status: GLOBAL_STATUS, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowGlobalStatus"
}

case class ShowPlayerStatus(status: PLAYER_STATUS, player: AbstractPlayer, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowPlayerStatus"
}

case class ShowErrorStatus(status: ERROR_STATUS, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowErrorStatus"
}

case class ShowRoundStatus(status: ROUND_STATUS, currentRound: Round, objects: Any*) extends ShowStatusEvent {
  override def id: String = "ShowRoundStatus"
}