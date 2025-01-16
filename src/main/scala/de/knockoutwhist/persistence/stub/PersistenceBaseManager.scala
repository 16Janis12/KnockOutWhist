package de.knockoutwhist.persistence.stub

import de.knockoutwhist.events.ui.GameState
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint, PersistenceManager}
import de.knockoutwhist.rounds.{Match, Round, Trick}

object PersistenceBaseManager extends PersistenceManager {

  private var currentSnapshot: MatchSnapshot = MatchSnapshot()

  override def updateGameState(gameState: GameState): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withGameState(gameState)
    currentSnapshot
  }

  override def updateMatch(matchImpl: Match): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withMatchImpl(matchImpl)
    currentSnapshot
  }

  override def updateRound(round: Round): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withRound(round)
    currentSnapshot
  }

  override def updateTrick(trick: Trick): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withTrick(trick)
    currentSnapshot
  }

  override def updateCurrentIndex(currentIndex: Int): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withCurrentIndex(currentIndex)
    currentSnapshot
  }

  override def updateMethodEntryPoint(methodEntryPoint: MethodEntryPoint): MatchSnapshot = {
    currentSnapshot = currentSnapshot.withMethodEntryPoint(methodEntryPoint)
    currentSnapshot
  }

}
