package de.knockoutwhist.control.sublogic

import de.knockoutwhist.undo.Command

trait UndoManager {

  def doStep(command: Command): Unit
  def undoStep(): Unit
  def redoStep(): Unit

}
