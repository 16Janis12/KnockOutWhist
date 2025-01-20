package de.knockoutwhist.undo

trait Command {
  def doStep(): Unit
  def undoStep(): Unit
}
