package de.knockoutwhist.rounds

import de.knockoutwhist.cards.{CardManager, Player}

case class Match(totalplayers: List[Player], private var number_of_cards: Int = 7) {

  private val roundlist: List[Round] = List[Round]()
  private var current_round: Option[Round] = None
  private[rounds] var dogLife = false

  def createRound(): Round = {
    provideCards
    if (number_of_cards == 7) {
      val random_trumpsuit = CardManager.nextCard().suit
      current_round = Some(new Round(random_trumpsuit, this, totalplayers))
    } else {
      val winner = roundlist.last.winner
      val trumpsuit = winner.pickTrumpsuit()
      
      current_round = Some(new Round(trumpsuit, this, roundlist.last.remainingPlayers()))
    }
    number_of_cards = number_of_cards - 1
    current_round.get
  }

  def isOver: Boolean = {
    if(roundlist.isEmpty) {
      false
    } else {
      roundlist.last.remainingPlayers().size == 1
    }
  }

  private def provideCards: Int = {
    CardManager.shuffleAndReset()
    var hands = 0
    for (player <- current_round.get.players_in) {
      player.provideHand(CardManager.createHand(number_of_cards))
      hands = hands + 1
    }
    hands
  }
}

