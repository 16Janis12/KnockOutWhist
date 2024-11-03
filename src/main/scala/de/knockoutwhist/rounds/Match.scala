package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{CardManager, Player}

import scala.collection.mutable.ListBuffer
import de.knockoutwhist.utils.Implicits._

case class Match(totalplayers: List[Player], private[rounds] var number_of_cards: Int = 7) {

  private[rounds] val roundlist: ListBuffer[Round] = ListBuffer[Round]()
  private var current_round: Option[Round] = None
  private[rounds] var dogLife = true

  def create_round(): Round = {
    val remainingPlayer = roundlist.isEmpty ? totalplayers |: roundlist.last.remainingPlayers()
    provideCards(remainingPlayer)
    if (roundlist.isEmpty) {
      val random_trumpsuit = CardManager.nextCard().suit
      current_round = Some(new Round(random_trumpsuit, this, remainingPlayer, true))
    } else {
      val winner = roundlist.last.winner
      val trumpsuit = winner.pickTrumpsuit()
      
      current_round = Some(new Round(trumpsuit, this, remainingPlayer, false))
    }
    number_of_cards -= 1
    current_round.get
  }

  def isOver: Boolean = {
    if(roundlist.isEmpty) {
      false
    } else {
      roundlist.last.remainingPlayers().size == 1
    }
  }

  private def provideCards(players: List[Player]): Int = {
    if(!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    var hands = 0
    for (player <- players) {
      player.provideHand(CardManager.createHand(number_of_cards))
      hands += 1
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

