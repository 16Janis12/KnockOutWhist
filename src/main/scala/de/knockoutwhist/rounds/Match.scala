package de.knockoutwhist.rounds
import de.knockoutwhist.cards.{CardManager, Player, Suit}
import de.knockoutwhist.rounds.Round
import de.knockoutwhist.cards.Player

import scala.collection.mutable.ListBuffer
import scala.util.Random
case class Match(totalplayers: List[Player]) {
  var number_of_cards = 7
  val roundlist: List[Round] = List[Round]()
  var current_round: Option[Round] = None

  def createRound(): Round = {
    provideCards()
    if (number_of_cards.equals(7)) {
      val random_trumpsuit = CardManager.nextCard().suit
     current_round = Some(new Round(random_trumpsuit, number_of_cards, totalplayers))
    } else {
      number_of_cards = number_of_cards - 1
      val winner = roundlist.last.winner
      val trumpsuit = winner.pickTrumpsuit()
      
      current_round = Some(new Round(trumpsuit, number_of_cards, roundlist.last.remainingPlayers()))
    }
    current_round.get
  }
  def provideCards(): Int = {
    CardManager.shuffleAndReset()
    var hands = 0
    for (player <- current_round.get.players_in) {
      player.provideHand(CardManager.createHand(number_of_cards))
      hands = hands + 1
    }
    hands
  }
}

