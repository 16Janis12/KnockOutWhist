package de.knockoutwhist.rounds
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.cards.Player
import de.knockoutwhist.cards.Card

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Round private(trumpSuit: Suit, cardAmount: Int, tricklist: ListBuffer[Trick], players_in: List[Player], players_out: List[Player] = null, winner: Player = null) {
  def this(trumpSuit: Suit, cardAmount: Int, players_in: List[Player]) = {
    this(trumpSuit, cardAmount, ListBuffer[Trick](), players_in)
  }
    
    
  
  def create_trick(): Trick = {
    new Trick(this)
  }
  
      
  def remainingPlayers(): List[Player] = {
    if (players_out == null) {
      return players_in
    }
    players_in.filter(!players_out.contains(_))
  }

  
  
}
