package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.BasePlayerTieLogic
import de.knockoutwhist.control.sublogic.{PersistenceManager, PlayerTieLogic}
import de.knockoutwhist.control.{GameLogic, LogicSnapshot}
import de.knockoutwhist.events.util.ReloadAllEvent
import de.knockoutwhist.persistence.formats.JSONFormatter
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint}
import de.knockoutwhist.rounds.{Match, Round, Trick}

class BasePersistenceManager(val gameLogic: BaseGameLogic) extends PersistenceManager {

  private var currentSnapshot: MatchSnapshot = MatchSnapshot()

  override def update(methodEntryPoint: MethodEntryPoint): MatchSnapshot = {
    currentSnapshot = currentSnapshot
      .withMethodEntryPoint(methodEntryPoint)
      .withGameLogicSnapShot(gameLogic.createSnapshot().asInstanceOf[LogicSnapshot[GameLogic]])
    currentSnapshot
  }

  override def saveFile(path: String): Unit = {
    KnockOutWhist.config.fileFormatter.writeToFile(currentSnapshot, path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
  }

  override def loadFile(path: String): Unit = {
    currentSnapshot = KnockOutWhist.config.fileFormatter.readFromFile(path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
    if currentSnapshot.entryPoint.isEmpty then
      throw new IllegalStateException("Loaded snapshot does not contain an entry point!")
    currentSnapshot.gameLogicSnapShot.foreach(_.restore(gameLogic))
    gameLogic.invoke(ReloadAllEvent())
    currentSnapshot.entryPoint.get match {
      case MethodEntryPoint.ControlMatch =>
        gameLogic.controlMatch()
      case MethodEntryPoint.ControlRound =>
        gameLogic.controlRound()
      case MethodEntryPoint.ControlTrick =>
        gameLogic.controlTrick()
    }
  }

}
