package de.knockoutwhist.utils

trait CustomPlayerQueue[A] extends Iterable[A] {
  /**
   * Returns the current index of the player queue.
   * @return the current index of the player queue.
   */
  def currentIndex: Int

  /**
   * Returns the current player of the player queue.
   * @return the current player of the player queue.
   */
  def nextPlayer(): A

  /**
   * Removes the player from the player queue.
   * @param player the player to remove.
   * @return the player object
   */
  def remove(player: A): Int

  /**
   * Resets the current index and sets the player as the start.
   * @param player the player to set as the start.
   * @return true if the player was found and set as the start, false otherwise.
   */
  def resetAndSetStart(player: A): Boolean

  /**
   * Let's the iterator start at a specific index.
   * @param start the index to start at.
   * @return the iterator.
   */
  def iteratorWithStart(start: Int = 0): Iterator[A]

  /**
   * Returns an iterator that starts from the beginning.
   * @return the iterator.
   */
  def fromFirstIterator: Iterator[A]

  /**
   * Duplicates the player queue.
   * @return the duplicated player queue.
   */
  def duplicate(): CustomPlayerQueue[A]
}
