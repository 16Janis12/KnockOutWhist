package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.control.{GameLogic, LogicSnapshot}

object SnapshotUtil {
  
  def generateSnapshots(gameLogic: GameLogic): (LogicSnapshot[? <: GameLogic], LogicSnapshot[? <: GameLogic]) = {
    val gameLogicSnapshot = gameLogic.createSnapshot()
    val playerTieLogicSnapshot = gameLogic.playerTieLogic.createSnapshot()
    (gameLogicSnapshot, playerTieLogicSnapshot.asInstanceOf[LogicSnapshot[? <: GameLogic]])
  }

}
