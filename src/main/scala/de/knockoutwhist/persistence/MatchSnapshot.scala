package de.knockoutwhist.persistence

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, ControlThread}
import de.knockoutwhist.events.old.ui.{GameState, GameStateUpdateEvent}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

enum MethodEntryPoint:
  case ControlMatch
  case ControlRound
  case ControlTrick
end MethodEntryPoint


case class MatchSnapshot(
                          matchImpl: Option[Match] = None,
                          round: Option[Round] = None,
                          trick: Option[Trick] = None,
                          currentIndex: Option[Int] = None,
                          gameState: GameState = GameState.MAIN_MENU,
                          methodEntryPoint: Option[MethodEntryPoint] = None
                        ) {

  def withMatchImpl(matchImpl: Match): MatchSnapshot = copy(matchImpl = Some(matchImpl))
  def withRound(round: Round): MatchSnapshot = copy(round = Some(round))
  def withTrick(trick: Trick): MatchSnapshot = copy(trick = Some(trick))
  def withCurrentIndex(currentIndex: Int): MatchSnapshot = copy(currentIndex = Some(currentIndex))
  def withGameState(gameState: GameState): MatchSnapshot = copy(gameState = gameState)
  def withMethodEntryPoint(methodEntryPoint: MethodEntryPoint): MatchSnapshot = copy(methodEntryPoint = Some(methodEntryPoint))

  def restoreSnapshot(): Unit = {
    ControlThread.runLater {
      ControlHandler.invoke(GameStateUpdateEvent(gameState))
      methodEntryPoint match {
        case Some(MethodEntryPoint.ControlMatch) => KnockOutWhist.config.maincomponent.controlMatch(matchImpl.get)
        case Some(MethodEntryPoint.ControlRound) => KnockOutWhist.config.maincomponent.controlRound(matchImpl.get, round.get)
        case Some(MethodEntryPoint.ControlTrick) => KnockOutWhist.config.maincomponent.controlTrick(matchImpl.get, round.get, trick.get, currentIndex.get)
        case _ => throw new RuntimeException("MethodEntryPoint not found")
      }
    }
  }
}

