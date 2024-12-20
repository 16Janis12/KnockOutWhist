package de.knockoutwhist.utils

trait CustomPlayerQueueComponent[A] extends Iterable[A] {
  def currentIndex: Int

  def nextPlayer(): A

  def remove(player: A): Int

  def resetAndSetStart(player: A): Boolean

  def iteratorWithStart(start: Int = 0): Iterator[A]

  def fromFirstIterator: Iterator[A]

}
