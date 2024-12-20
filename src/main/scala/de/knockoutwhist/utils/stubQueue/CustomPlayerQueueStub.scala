package de.knockoutwhist.utils.stubQueue

import de.knockoutwhist.utils.CustomPlayerQueue

import scala.compiletime.uninitialized

class CustomPlayerQueueStub[A](protected var players: Array[A], val start: Int = 0) extends CustomPlayerQueue[A] {

  private var current = start

  def currentIndex: Int = current

  def nextPlayer(): A = {
    players(0)
  }
  def remove(player: A): Int = {
   5
  }
  def resetAndSetStart(player: A): Boolean = {
    false
  }

  override def toList: List[A] = List()

  override def isEmpty: Boolean = false

  override def size: Int = 1

  override def duplicate(): CustomPlayerQueue[A] = new CustomPlayerQueueStub(players, 0)

  def iterator: Iterator[A] = new Iterator[A] {
    override def hasNext: Boolean = true

    override def next(): A = players(0)
  }

  def iteratorWithStart(start: Int = 0): Iterator[A] = new Iterator[A] {
    override def hasNext: Boolean = true
    
    override def next(): A = players(0)
  }

  def fromFirstIterator: Iterator[A] = new Iterator[A] {
  
    override def hasNext: Boolean = true

    override def next(): A = players(0)
  }
}


