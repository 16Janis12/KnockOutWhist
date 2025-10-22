package de.knockoutwhist.control

/**
 * Test stub for ControlThread so tests run synchronously.
 */
object ControlThread {
  def runLater[R](op: => R): Unit = op
  def start(): Unit = {}
}

