package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.rounds.{Round, Trick}

import scala.util.Try

abstract case class AbstractPlayer private[player](var name: String, hand: Option[Hand], doglife: Boolean = false) {
  def currentHand(): Option[Hand] = hand
  
  def provideHand(hand: Hand): AbstractPlayer
  def setDogLife(): AbstractPlayer
  def removeCard(card: Card): AbstractPlayer

  def handlePlayCard(hand: Hand, round: Round, trick: Trick): Try[Card]
  def handleDogPlayCard(hand: Hand,round: Round, trick: Trick, needstoplay: Boolean): Try[Option[Card]]
  def handlePickTrumpsuit(): Try[Suit]
  def handlePickTieCard(min: Int, max: Int): Try[Int]
  
  override def toString: String = {
    name
  }
  
}
