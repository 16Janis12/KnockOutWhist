package de.knockoutwhist.player

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try

abstract case class AbstractPlayer private[player](var name: String, hand: Option[Hand], doglife: Boolean = false) {
  
  //If you get an uuid conflict, go play the lottery!!! 
  val id: UUID = UUID.randomUUID()
  def currentHand(): Option[Hand] = hand
  
  def provideHand(hand: Hand): AbstractPlayer
  def setDogLife(): AbstractPlayer
  def removeCard(card: Card): AbstractPlayer

  def handlePlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int): Unit
  def handleDogPlayCard(hand: Hand, matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, needstoplay: Boolean): Unit
  def handlePickTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean): Unit
  def handlePickTieCard(winner: List[AbstractPlayer], matchImpl: Match, round: Round, playersout: List[AbstractPlayer], cut: immutable.HashMap[AbstractPlayer, Card], currentStep: Int, remaining: Int, currentIndex: Int = 0): Unit
  
  override def toString: String = {
    name
  }
  
}
