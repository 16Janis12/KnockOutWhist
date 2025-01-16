package de.knockoutwhist.utils

import java.util.UUID
import scala.collection.mutable

abstract class CustomThread extends Thread {

  private val processesToRunLater: mutable.ArrayDeque[Runnable] = mutable.ArrayDeque()
  private val results: mutable.HashMap[UUID, Any] = mutable.HashMap()

  override def run(): Unit = {
    while (true) {
      var process: Option[Runnable] = None
      this.synchronized {
        if (processesToRunLater.nonEmpty) {
          process = Some(processesToRunLater.removeHead())
        } else {
          this.wait()
        }
      }
      process match {
        case Some(p) => p.run()
        case None =>
      }
    }
  }

  def instance: CustomThread

  def runLater[R](op: => R): Unit = {
    this.synchronized {
      processesToRunLater.append(new Runnable {
        override def run(): Unit = {
          op
        }
      })
      instance.notify()
    }
  }

  def runNow[R](op: => R): Option[R] = {
    if (Thread.currentThread() == this) {
      Some(op)
    } else {
      val currentThread = Thread.currentThread()
      val uuid = UUID.randomUUID()
      this.synchronized {
        processesToRunLater.append(new Runnable {
          override def run(): Unit = {
            results.put(uuid, op)
            currentThread.synchronized {
              currentThread.notify()
            }
          }
        })
        instance.notify()
      }
      currentThread.wait()
      if (results.contains(uuid)) {
        return Some(results.remove(uuid).get.asInstanceOf[R])
      }
      None
    }
  }

}
