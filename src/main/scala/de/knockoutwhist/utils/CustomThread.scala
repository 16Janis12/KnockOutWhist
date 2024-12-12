package de.knockoutwhist.utils

import scala.collection.mutable

abstract class CustomThread extends Thread {

  private val processesToRun: mutable.ArrayDeque[Runnable] = mutable.ArrayDeque()

  override def run(): Unit = {
    while (true) {
      if (processesToRun.nonEmpty) {
        this.synchronized {
          val process = processesToRun.removeHead()
          process.run()
        }
      }
    }
  }

  def runLater[R](op: => R): Unit = {
    this.synchronized {
      processesToRun.append(new Runnable {
        override def run(): Unit = {
          op
        }
      })
    }
  }
}
