package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Round (trumpSuit: Suit, matchImpl: Match, tricklist: ListBuffer[Trick], playersin: List[Player], playersout: List[Player] = null, winner: Player = null, firstRound: Boolean) {
  def this(trumpSuit: Suit, matchImpl: Match, playersin: List[Player], firstRound: Boolean) = {
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
