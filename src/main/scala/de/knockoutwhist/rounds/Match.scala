package de.knockoutwhist.rounds

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*

import scala.collection.immutable.List
import scala.util.Random

case class Match(totalplayers: List[AbstractPlayer], var numberofcards: Int = 7, startingPlayer: Int = -1, dogLife: Boolean = false, roundlist: List[Round] = List[Round]()) {

  val playerQueue: CustomPlayerQueue[AbstractPlayer] = CustomPlayerQueue[AbstractPlayer](
    totalplayers.toArray,
    (startingPlayer == -1) ? Random.nextInt(totalplayers.length) |: startingPlayer
  )
  
  def addRound(round: Round): Match = {
    this.copy(startingPlayer = playerQueue.currentIndex,roundlist = roundlist :+ round)
  }
  
  def setDogLife(): Match = {
    this.copy(startingPlayer = playerQueue.currentIndex, dogLife = true)
  }
  
  def updatePlayers(totalplayers: List[AbstractPlayer]): Match = {
    this.copy(startingPlayer = playerQueue.currentIndex, totalplayers = totalplayers)
  }
  
  override def toString: String = {
    s"$totalplayers, $numberofcards"
  }
}

