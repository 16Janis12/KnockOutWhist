package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.player.Player

import scala.collection.mutable.ListBuffer
import de.knockoutwhist.utils.Implicits._

case class Match(totalplayers: List[Player], var number_of_cards: Int = 7) {

  val roundlist: ListBuffer[Round] = ListBuffer[Round]()
  var current_round: Option[Round] = None
  var dogLife = false

  override def toString: String = {
    s"${totalplayers}, ${number_of_cards}"
  }
}

