package de.knockoutwhist.rounds

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue

import scala.collection.immutable
import scala.collection.immutable.List
import scala.util.Random

case class Round (trumpSuit: Suit, tricklist: List[Trick], playersin: List[AbstractPlayer], playersout: List[AbstractPlayer] = null, startingPlayer: Int = -1, winner: AbstractPlayer = null, firstRound: Boolean) {
  def this(trumpSuit: Suit, playersin: List[AbstractPlayer], firstRound: Boolean) = {
    this(trumpSuit, List[Trick](), playersin, firstRound = firstRound)
  }

  val playerQueue: CustomPlayerQueue[AbstractPlayer] = CustomPlayerBaseQueue[AbstractPlayer](
    playersin.toArray,
    (startingPlayer == -1) ? Random.nextInt(playersin.length) |: startingPlayer
  )
  
  def addTrick(trick: Trick): Round = {
    Round(trumpSuit, tricklist :+ trick, playersin, playersout, playerQueue.currentIndex, winner, firstRound)
  }
  
  def updatePlayersIn(playersin: List[AbstractPlayer]): Round = {
    Round(trumpSuit, tricklist, playersin, playersout, playerQueue.currentIndex, winner, firstRound)
  }
  
  override def toString: String = {
    s"$trumpSuit, $tricklist, $playersin, $playersout, $winner, $firstRound"
  }
  
  
}
