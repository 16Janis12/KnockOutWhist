package de.knockoutwhist.control

object ControlThread {

  def runLater[R](op: => R): Unit = {
    op
  }
  
}
