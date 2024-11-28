package de.knockoutwhist.player

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, Hand, Suit}

import scala.collection.mutable.ListBuffer

abstract case class AbstractPlayer(name: String) {
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

  def handleAction(action: InputAction, objects: Any*): Boolean 
  
  
  override def toString: String = {
    name
  }
  
  
  
  
  
}
