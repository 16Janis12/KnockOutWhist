package de.knockoutwhist.rounds
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{CardManager, Suit}
import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Round (trumpSuit: Suit, matchImpl: Match, tricklist: ListBuffer[Trick], players_in: List[Player], players_out: List[Player] = null, winner: Player = null, firstRound: Boolean) {
  def this(trumpSuit: Suit, matchImpl: Match, players_in: List[Player], firstRound: Boolean) = {
    this(trumpSuit, matchImpl, ListBuffer[Trick](), players_in, firstRound = firstRound)
  }

  private var current_trick: Option[Trick] = None

  def get_current_trick(): Option[Trick] = {
    current_trick
  }
  
  def set_current_trick(trick: Trick): Unit = {
    current_trick = Some(trick)
  }

  def get_tricks(): List[Trick] = tricklist.toList
  
  override def toString: String = {
    s"$trumpSuit, $tricklist, $players_in, $players_out, $winner, $firstRound"
  }
  
  
}
