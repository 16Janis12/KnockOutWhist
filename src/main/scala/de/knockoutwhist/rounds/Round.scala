package de.knockoutwhist.rounds
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.cards.Suit
import de.knockoutwhist.cards.Player
import scala.collection.mutable.ListBuffer

case class Round(trumpSuit: Suit, cardAmount: Int, tricklist: ListBuffer[Trick], players_in: List[Player]) {
  def create_trick(trumpSuit: Suit, players_in: List[Player]): Unit = 
  {
    
  }  
  
  
  
}
