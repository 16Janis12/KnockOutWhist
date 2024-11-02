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
  def removeCard(card: Card): Boolean = {
    val hand_new = ListBuffer[Card]()
    for(cardl <- hand.get.cards) {
      if (!(cardl == card)) {
        hand_new.addOne(cardl)
      }
    }
    val hand1 = Hand(hand_new)
    if(hand.get == hand1) {
      false
    } else {
      true
    }
  }
  
  
  
  
}
