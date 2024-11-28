package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Round (trumpSuit: Suit, matchImpl: Match, tricklist: ListBuffer[Trick], playersin: List[AbstractPlayer], playersout: List[AbstractPlayer] = null, winner: AbstractPlayer = null, firstRound: Boolean) {
  def this(trumpSuit: Suit, matchImpl: Match, playersin: List[AbstractPlayer], firstRound: Boolean) = {
    this(trumpSuit, matchImpl, ListBuffer[Trick](), playersin, firstRound = firstRound)
  }

  private var currenttrick: Option[Trick] = None

  def getcurrenttrick(): Option[Trick] = {
    currenttrick
  }
  
  def setcurrenttrick(trick: Trick): Unit = {
    currenttrick = Some(trick)
  }
  
  override def toString: String = {
    s"$trumpSuit, $tricklist, $playersin, $playersout, $winner, $firstRound"
  }
  
  
}
