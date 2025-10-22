package de.knockoutwhist.rounds

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.Implicits.*

import java.util.UUID
import scala.collection.immutable.List

case class Match(totalplayers: List[AbstractPlayer], playersIn: List[AbstractPlayer] = List(), numberofcards: Int = 7, dogLife: Boolean = false, roundlist: List[Round] = List[Round]()) {
  
  def addRound(round: Round): Match = {
    this.copy(roundlist = roundlist :+ round)
  }
  
  def setNumberOfCards(numberofcards: Int): Match = {
    this.copy(numberofcards = numberofcards)
  }
  
  def setDogLife(): Match = {
    this.copy(dogLife = true)
  }
  
  def updatePlayersIn(playersIn: List[AbstractPlayer]): Match = {
    this.copy(playersIn = playersIn)
  }
  
  def playersOut: List[AbstractPlayer] = {
    totalplayers.diff(playersIn)
  }
  
  def isOver: Boolean = {
    playersIn.size <= 1
  }
  
  override def toString: String = {
    s"$totalplayers, $numberofcards"
  }
}

