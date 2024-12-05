package de.knockoutwhist.undo

object UndoManager {

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    undoStack = command :: undoStack
    command.doStep()
  }

  def undoStep(): Boolean = {
    undoStack match {
      case Nil => false
      case head :: stack =>
        head.undoStep()
        undoStack = stack
        redoStack = head :: redoStack
        true
    }
  }
  
  def redoStep(): Boolean = {
    redoStack match {
      case Nil => false
      case head :: stack =>
        head.doStep()
        redoStack = stack
        undoStack = head :: undoStack
        true
    }
  }

}