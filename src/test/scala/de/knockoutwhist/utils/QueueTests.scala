package de.knockoutwhist.utils

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QueueTests extends AnyWordSpec with Matchers {
  "A queue" should {

    "return the next player" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.nextPlayer() must be(1)
      queue.nextPlayer() must be(2)
      queue.nextPlayer() must be(3)
      queue.nextPlayer() must be(4)
      queue.nextPlayer() must be(5)
      queue.nextPlayer() must be(1)
    }
    "remove a player" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.remove(3) must be(4)
      queue.nextPlayer() must be(1)
      queue.nextPlayer() must be(2)
      queue.nextPlayer() must be(4)
      queue.nextPlayer() must be(5)
    }
    "reset and set start" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.resetAndSetStart(5) must be(true)
      queue.nextPlayer() must be(5)
      queue.nextPlayer() must be(1)
      queue.nextPlayer() must be(2)
      queue.nextPlayer() must be(3)
      queue.nextPlayer() must be(4)
    }
    "reset and set start from an invalid number" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.resetAndSetStart(6) must be(false)
      queue.nextPlayer() must be(1)
      queue.nextPlayer() must be(2)
      queue.nextPlayer() must be(3)
      queue.nextPlayer() must be(4)
      queue.nextPlayer() must be(5)
    }
    "return a list" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.toList must be(List(1, 2, 3, 4, 5))
    }
    "be empty" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 4, 5))
      queue.isEmpty must be(false)
      queue.remove(1)
      queue.remove(4)
      queue.remove(5)
      queue.isEmpty must be(true)
    }
    "return the size" in {
      var queue = new CustomPlayerQueue[Int](Array())
      queue.size must be(0)
      queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.size must be(5)
    }
    "iterate over the queue" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      val iterator = queue.iterator
      iterator.next() must be(1)
      iterator.next() must be(2)
      iterator.next() must be(3)
      iterator.next() must be(4)
      iterator.next() must be(5)
      iterator.hasNext must be(false)
    }
  }
}
