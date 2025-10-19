package de.knockoutwhist.control.sublogic

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.player.AbstractPlayer

trait PlayerInputLogic {

  def requestTrumpSuit(player: AbstractPlayer): Unit
  def receivedTrumpSuit(suit: Suit): Unit
  def requestCard(player: AbstractPlayer): Unit
  def receivedCard(card: Card): Unit
  def receivedDog(dog: Option[Card]): Unit

  def isWaitingForInput: Boolean
  

}
