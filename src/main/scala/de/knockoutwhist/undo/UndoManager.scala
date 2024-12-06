package de.knockoutwhist.undo

object UndoManager {

  private var undoStack: List[Command] = Nil
  private var redoStack: List[Command] = Nil

  def doStep(command: Command): Unit = {
    redoStack = Nil
    undoStack = command :: undoStack
    command.doStep()
  }

  def undoStep(): Boolean = {
    undoStack match {
      case Nil => false
      case head :: stack =>
        undoStack = stack
        redoStack = head :: redoStack
        head.undoStep()
        true
    }
  }
  
  def redoStep(): Boolean = {
    redoStack match {
      case Nil => false
      case head :: stack =>
        redoStack = stack
        undoStack = head :: undoStack
        head.doStep()
        true
    }
  }

}