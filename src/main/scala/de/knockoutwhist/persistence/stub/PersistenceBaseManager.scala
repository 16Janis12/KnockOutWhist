package de.knockoutwhist.persistence.stub

import de.knockoutwhist.events.ui.GameState
import de.knockoutwhist.persistence.formats.XMLFormatter
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint, PersistenceManager}
import de.knockoutwhist.rounds.{Match, Round, Trick}

object PersistenceBaseManager extends PersistenceManager {

  private var currentSnapshot: MatchSnapshot = MatchSnapshot()
  private val XMLFormatter = new XMLFormatter()

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
    XMLFormatter.writeToFile(currentSnapshot, path)
  }

  override def loadFile(path: String): Unit = {
    currentSnapshot = XMLFormatter.readFromFile(path)
    currentSnapshot.restoreSnapshot()
  }

}
