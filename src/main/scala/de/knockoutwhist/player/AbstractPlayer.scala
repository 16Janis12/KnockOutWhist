package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand}

import java.util.UUID

//If you get an uuid conflict, go play the lottery!!!
abstract case class AbstractPlayer private[player](name: String, id: UUID = UUID.randomUUID()) {
  
  protected var hand: Option[Hand] = None
  protected var dogLife: Boolean = false
  
  def currentHand(): Option[Hand] = hand
  
  def isInDogLife: Boolean = dogLife
  
  def provideHand(hand: Hand): Unit = {
    this.hand = Some(hand)
  }
  def setDogLife(): Unit = {
    this.dogLife = true
  }
  def resetDogLife(): Unit = {
    this.dogLife = false
  }
  def removeCard(card: Card): Unit = {
    this.hand = this.hand.map(_.removeCard(card))
  }

  override def toString: String = {
    name
  }

  override def canEqual(that: Any): Boolean = that.isInstanceOf[AbstractPlayer]

  override def equals(obj: Any): Boolean = {
    obj match {
      case that: AbstractPlayer => this.id.equals(that.id)
      case _ => false
    }
  }
  
  def generatePlayerData(): PlayerData = {
    PlayerData(id, name, hand, dogLife)
  }
  
  def receivePlayerData(data: PlayerData): Unit = {
    if (this.id != data.id) {
      throw new IllegalArgumentException("Cannot receive PlayerData for a different player!")
    }
    if (this.name != data.name) {
      throw new IllegalArgumentException("Cannot change player name via PlayerData!")
    }
    this.hand = data.hand
    this.dogLife = data.dogLife
  }
  
}

case class PlayerData(id: UUID, name: String, hand: Option[Hand], dogLife: Boolean) {
  
}

