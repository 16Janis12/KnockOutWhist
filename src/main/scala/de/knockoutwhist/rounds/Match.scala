package de.knockoutwhist.rounds

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable.ListBuffer

case class Match(totalplayers: List[AbstractPlayer], var numberofcards: Int = 7) {

  val roundlist: ListBuffer[Round] = ListBuffer[Round]()
  var current_round: Option[Round] = None
  var dogLife = false

  override def toString: String = {
    s"$totalplayers, $numberofcards"
  }
}

