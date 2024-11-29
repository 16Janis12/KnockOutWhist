package de.knockoutwhist.utils

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class QueueTests extends AnyWordSpec with Matchers {
  "A queue" should {

    "return the next player" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.nextPlayer() should be(1)
      queue.nextPlayer() should be(2)
      queue.nextPlayer() should be(3)
      queue.nextPlayer() should be(4)
      queue.nextPlayer() should be(5)
      queue.nextPlayer() should be(1)
    }
    "remove a player" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.remove(3) should be(4)
      queue.nextPlayer() should be(1)
      queue.nextPlayer() should be(2)
      queue.nextPlayer() should be(4)
      queue.nextPlayer() should be(5)
    }
    "reset and set start" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.resetAndSetStart(5) should be(true)
      queue.nextPlayer() should be(5)
      queue.nextPlayer() should be(1)
      queue.nextPlayer() should be(2)
      queue.nextPlayer() should be(3)
      queue.nextPlayer() should be(4)
    }
    "reset and set start from an invalid number" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.resetAndSetStart(6) should be(false)
      queue.nextPlayer() should be(1)
      queue.nextPlayer() should be(2)
      queue.nextPlayer() should be(3)
      queue.nextPlayer() should be(4)
      queue.nextPlayer() should be(5)
    }
    "return a list" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.toList should be(List(1, 2, 3, 4, 5))
    }
    "be empty" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 4, 5))
      queue.isEmpty should be(false)
      queue.remove(1)
      queue.remove(4)
      queue.remove(5)
      queue.isEmpty should be(true)
    }
    "return the size" in {
      var queue = new CustomPlayerQueue[Int](Array())
      queue.size should be(0)
      queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      queue.size should be(5)
    }
    "iterate over the queue" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      val iterator = queue.iterator
      iterator.next() should be(1)
      iterator.next() should be(2)
      iterator.next() should be(3)
      iterator.next() should be(4)
      iterator.next() should be(5)
      iterator.hasNext should be(false)
    }
    "iterate with second iterator over the queue" in {
      val queue = new CustomPlayerQueue[Int](Array(1, 2, 3, 4, 5))
      val iterator = queue.fromFirstIterator
      iterator.next() should be(1)
      iterator.next() should be(2)
      iterator.next() should be(3)
      iterator.next() should be(4)
      iterator.next() should be(5)
      iterator.hasNext should be(false)
    }
  }
}
