package de.knockoutwhist.persistence.stub

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.events.old.ui.GameState
import de.knockoutwhist.persistence.formats.{JSONFormatter, XMLFormatter}
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

  override def saveFile(path: String): Unit = {
    KnockOutWhist.config.fileFormatter.writeToFile(currentSnapshot, path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
  }

  override def loadFile(path: String): Unit = {
    currentSnapshot = KnockOutWhist.config.fileFormatter.readFromFile(path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
    currentSnapshot.restoreSnapshot()
  }

}
