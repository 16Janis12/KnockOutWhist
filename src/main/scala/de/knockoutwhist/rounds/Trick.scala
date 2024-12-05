package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer

import scala.collection.immutable


case class Trick (round: Round, cards: immutable.HashMap[Card, AbstractPlayer], winner: AbstractPlayer = null, finished: Boolean = false, firstCard: Option[Card] = None) {
  
  def this(round: Round) = {
    this(round, immutable.HashMap[Card, AbstractPlayer]())
  }
  var remainingPlayers: Int = round.playersin.size

  def addCard(card: Card, player: AbstractPlayer): Trick = {
    Trick(round, cards + (card -> player), winner, finished, firstCard)
  }
  
  def setfirstcard(card: Card): Trick = {
    if(firstCard.isDefined) {
      throw new IllegalStateException("This trick is already finished")
    }
    Trick(round, cards, winner, finished, Some(card))
  }

  override def toString: String = {
    s"$cards, $winner, $finished"
  }
}


