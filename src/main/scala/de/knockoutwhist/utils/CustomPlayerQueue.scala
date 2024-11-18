package de.knockoutwhist.utils


class CustomPlayerQueue[A] (protected var players: Array[A], val start: Int = 0) extends Iterable[A] {

  private var current = start
  
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
  
  

  def iterator: Iterator[A] = new Iterator[A] {
    private var index = 0
    def hasNext: Boolean = index < players.length
    def next(): A = {
      index += 1
      CustomPlayerQueue.this.nextPlayer()
    }
  }
}
