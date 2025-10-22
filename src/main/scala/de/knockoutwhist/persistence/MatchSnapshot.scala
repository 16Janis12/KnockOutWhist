package de.knockoutwhist.persistence

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.BasePlayerTieLogic
import de.knockoutwhist.control.sublogic.PlayerTieLogic
import de.knockoutwhist.control.{ControlThread, GameLogic, LogicSnapshot}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

enum MethodEntryPoint:
  case ControlMatch
  case ControlRound
  case ControlTrick
end MethodEntryPoint


case class MatchSnapshot(
                          entryPoint: Option[MethodEntryPoint] = None,
                          gameLogicSnapShot: Option[LogicSnapshot[GameLogic]] = None
                        ) {
  def withMethodEntryPoint(newEntryPoint: MethodEntryPoint): MatchSnapshot = this.copy(entryPoint = Some(newEntryPoint))
  def withGameLogicSnapShot(newGameLogicSnapShot: LogicSnapshot[GameLogic]): MatchSnapshot = this.copy(gameLogicSnapShot = Some(newGameLogicSnapShot))
}

