package de.knockoutwhist.cards

import de.knockoutwhist.KnockOutWhist

import scala.collection.mutable.ListBuffer

case class Player(name: String) {
  private var hand: Option[Hand] = None

  def currentHand(): Option[Hand] = hand
  var doglife: Boolean = false
  def provideHand(hand: Hand): Boolean = {
    this.hand = Some(hand)
    true
  }
  def pickTrumpsuit(): Suit = {
    KnockOutWhist.matchControl.playerControl.pickNextTrumpsuit(this)
  }
  def removeCard(card: Card): Int = {
    hand = Some(hand.get.removeCard(card))
    hand.get.cards.size
  }

  override def toString: String = {
    name
  }
  
  
  
  
}
