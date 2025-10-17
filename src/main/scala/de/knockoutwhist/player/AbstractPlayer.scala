package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try

//If you get an uuid conflict, go play the lottery!!!
abstract case class AbstractPlayer private[player](name: String, id: UUID = UUID.randomUUID()) {
  
  protected var hand: Option[Hand] = None
  protected var doglife: Boolean = false
  
  def currentHand(): Option[Hand] = hand
  
  def isInDoglife: Boolean = doglife
  
  def provideHand(hand: Hand): Unit = {
    this.hand = Some(hand)
  }
  def setDogLife(): Unit = {
    this.doglife = true
  }
  def resetDogLife(): Unit = {
    this.doglife = false
  }
  def removeCard(card: Card): Unit = {
    this.hand = this.hand.map(_.removeCard(card))
  }

  def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit
  def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit
  def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit
  def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit
  
  override def toString: String = {
    name
  }
  
}
