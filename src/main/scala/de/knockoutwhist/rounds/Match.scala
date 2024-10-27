package de.knockoutwhist.rounds
import de.knockoutwhist.cards.{CardManager, Player, Suit}
import de.knockoutwhist.rounds.Round

import scala.util.Random
case class Match(totalplayers: List[Player]) {
  var number_of_cards = 7
  val roundlist: List[Round] = List[Round]()

  def createRound(): Unit = {
    provideCards()
    if (number_of_cards.equals(7)) {
      val random_trumpsuit = CardManager.nextCard().suit
    } else {

    }
  }
  def provideCards(): Boolean = {
    if (roundlist.last.players_in.equals(0)) {
      false
    } else if (roundlist.last.players_in.equals(1)) {
      false
    } else {
      true
    }
  }
}

