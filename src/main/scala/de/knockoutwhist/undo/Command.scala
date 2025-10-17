package de.knockoutwhist.undo

import de.knockoutwhist.control.GameLogic

trait Command {
  def doStep(gameLogic: GameLogic): Unit
  def undoStep(gameLogic: GameLogic): Unit
}
