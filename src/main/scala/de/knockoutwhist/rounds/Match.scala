package de.knockoutwhist.rounds

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.Implicits.*

import java.util.UUID
import scala.collection.immutable.List

case class Match(id: UUID = UUID.randomUUID(), totalplayers: List[AbstractPlayer], numberofcards: Int = 7, dogLife: Boolean = false, roundlist: List[Round] = List[Round](), cardManager: CardManager) {
  
  def addRound(round: Round): Match = {
    this.copy(roundlist = roundlist :+ round)
  }
  
  def setNumberOfCards(numberofcards: Int): Match = {
    this.copy(numberofcards = numberofcards)
  }
  
  def setDogLife(): Match = {
    this.copy(dogLife = true)
  }
  
  def updatePlayers(totalplayers: List[AbstractPlayer]): Match = {
    this.copy(totalplayers = totalplayers)
  }
  
  override def toString: String = {
    s"$totalplayers, $numberofcards"
  }
}

