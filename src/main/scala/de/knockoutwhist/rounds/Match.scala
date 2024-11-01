package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{CardManager, Player}

import scala.collection.mutable.ListBuffer

case class Match(totalplayers: List[Player], private var number_of_cards: Int = 7) {

  private val roundlist: ListBuffer[Round] = ListBuffer[Round]()
  private var current_round: Option[Round] = None
  private[rounds] var dogLife = false

  def create_round(): Round = {
    if (number_of_cards == 7) {
      val random_trumpsuit = CardManager.nextCard().suit
      current_round = Some(new Round(random_trumpsuit, this, totalplayers))
      roundlist.addOne(current_round.get)
    } else {
      val winner = roundlist.last.winner
      val trumpsuit = winner.pickTrumpsuit()
      current_round = Some(new Round(trumpsuit, this, roundlist.last.remainingPlayers()))
      roundlist.addOne(current_round.get)
    }
    provideCards
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
    if(!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    var hands = 0
    for (player <- current_round.get.players_in) {
      player.provideHand(CardManager.createHand(number_of_cards))
      hands = hands + 1
    }
    hands
  }
  
  def finalizeMatch(): Player = {
    if(!isOver) {
      throw new IllegalStateException("Match is not over yet.")
    }
    roundlist.last.remainingPlayers().head
  }
  
}

