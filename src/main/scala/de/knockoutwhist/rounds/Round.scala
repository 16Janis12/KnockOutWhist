package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*

import scala.collection.immutable
import scala.collection.immutable.List
import scala.util.Random

case class Round (trumpSuit: Suit, firstRound: Boolean, tricklist: List[Trick] = List(), winner: Option[AbstractPlayer] = None) {
  
  def addTrick(trick: Trick): Round = {
    Round(trumpSuit, firstRound, tricklist :+ trick, winner)
  }
  
  override def toString: String = {
    s"$trumpSuit, $tricklist, $winner, $firstRound"
  }
  
  
}
