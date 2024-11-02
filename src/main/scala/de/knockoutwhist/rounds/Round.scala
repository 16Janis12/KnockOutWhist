package de.knockoutwhist.rounds
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Player, Suit}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import de.knockoutwhist.utils.Implicits._

case class Round private(trumpSuit: Suit, matchImpl: Match, private[rounds] val tricklist: ListBuffer[Trick], players_in: List[Player], players_out: List[Player] = null, winner: Player = null) {
  def this(trumpSuit: Suit, matchImpl: Match, players_in: List[Player]) = {
    this(trumpSuit, matchImpl, ListBuffer[Trick](), players_in)
  }
  
  def create_trick(): Trick = {
    new Trick(this)
  }

  def isOver: Boolean = {
    players_in.map(p => p.currentHand()).count(h => h.isEmpty) == 0
  }

  def finalizeRound(force: Boolean = false): (Player, Round) = {
    if(!force) {
      if(tricklist.isEmpty) {
        throw new IllegalStateException("No tricks played in this round")
      }else if(!isOver) {
        throw new IllegalStateException("Not all tricks were played in this round")
      }
    }
    val tricksMapped = tricklist
      .map(t => t.winner)
      .groupBy(identity).map((p, l) => (p, l.size)) //l.size = Anzahl gewonnener Tricks
    val winners = tricksMapped
      .filter((p, i) => i == tricksMapped.values.max)
      .keys

    var playersOut = players_in
      .filter(!tricksMapped.contains(_))

    if(playersOut.nonEmpty && !matchImpl.dogLife) {
      matchImpl.dogLife = true
      playersOut.foreach(p => p.doglife = true)
      playersOut = List()
    }

    val winner = (winners.size == 1)
      ? winners.head
      |: KnockOutWhist.matchControl.playerControl.determineWinnerTie(winners.toList)
    
    val finalRound = Round(trumpSuit, matchImpl, tricklist, players_in, players_out, winner)
    matchImpl.roundlist += finalRound
    (winner, finalRound)
  }
      
  def remainingPlayers(): List[Player] = {
    if (players_out == null) {
      return players_in
    }
    players_in.filter(!players_out.contains(_))
  }

  
  
}
