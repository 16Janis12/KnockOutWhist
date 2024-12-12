package de.knockoutwhist.undo

import de.knockoutwhist.control.ControlThread

object UndoManager {

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    redoStack = Nil
    undoStack = command :: undoStack
    command.doStep()
  }

  def undoStep(): Unit = {
    ControlThread.runLater {
      undoStack match {
        case Nil => false
        case head :: stack =>
          undoStack = stack
          redoStack = head :: redoStack
          try {
            head.undoStep()
          } catch {
            case _: UndoneException =>
          }
      }
    }
  }

  def redoStep(): Unit = {
    ControlThread.runLater {
      redoStack match {
        case Nil => false
        case head :: stack =>
          redoStack = stack
          undoStack = head :: undoStack
          try {
            head.doStep()
          } catch {
            case _: UndoneException =>
          }
      }
    }
  }

}