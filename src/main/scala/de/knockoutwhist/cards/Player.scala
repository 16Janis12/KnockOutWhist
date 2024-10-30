package de.knockoutwhist.cards

import de.knockoutwhist.KnockOutWhist

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
  
  
  
  
}
