package de.knockoutwhist.utils


class CustomPlayerQueue[A] (protected var players: Array[A], val start: Int = 0) extends Iterable[A] {

  private var current = start
  
  def currentIndex: Int = current
  
  def nextPlayer(): A = {
    val player = players(current)
    current = (current + 1) % players.length
    player
  }

  def remove(player: A): Int = {
    players = players.filter(_ != player)
    players.size
  }

  def resetAndSetStart(player: A): Boolean = {
    if(players.contains(player)) {
      current = players.indexOf(player)
      true
    } else {
      false
    }
  }
  
  override def toList: List[A] = players.toList
  override def isEmpty: Boolean = players.isEmpty
  override def size: Int = players.length
  override def clone(): CustomPlayerQueue[A] = new CustomPlayerQueue(players.clone(), current)
  

  def iterator: Iterator[A] = new QueueIterator[A](this)

  //Useful if start is not important
  def fromFirstIterator: Iterator[A] = new Iterator[A]:
    private val it: Iterator[A] = players.iterator
    override def hasNext: Boolean = it.hasNext

    override def next(): A = it.next()
}

class QueueIterator[A](queue: CustomPlayerQueue[A]) extends Iterator[A] {
  var index = 0
  def hasNext: Boolean = index < queue.size
  def next(): A = {
    index += 1
    queue.nextPlayer()
  }
}
