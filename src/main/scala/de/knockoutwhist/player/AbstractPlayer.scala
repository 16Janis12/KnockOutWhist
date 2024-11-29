package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.rounds.Trick

import scala.util.Try

abstract case class AbstractPlayer private[player](var name: String) {
  private var hand: Option[Hand] = None
  def currentHand(): Option[Hand] = hand
  var doglife: Boolean = false
  def provideHand(hand: Hand): Boolean = {
    this.hand = Some(hand)
    true
  }
  
  def removeCard(card: Card): Int = {
    hand = Some(hand.get.removeCard(card))
    hand.get.cards.size
  }

  def handlePlayCard(hand: Hand, trick: Trick): Try[Card]
  def handleDogPlayCard(hand: Hand, trick: Trick, needstoplay: Boolean): Try[Option[Card]]
  def handlePickTrumpsuit(): Try[Suit]
  def handlePickTieCard(min: Int, max: Int): Try[Int] 
  
  
  
  override def toString: String = {
    name
  }
  
  
  
  
  
}
