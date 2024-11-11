package de.knockoutwhist.rounds
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{CardManager, Suit}
import de.knockoutwhist.player.Player
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Round private[rounds](trumpSuit: Suit, matchImpl: Match, private[rounds] val tricklist: ListBuffer[Trick], players_in: List[Player], players_out: List[Player] = null, winner: Player = null, firstRound: Boolean) {
  def this(trumpSuit: Suit, matchImpl: Match, players_in: List[Player], firstRound: Boolean) = {
    this(trumpSuit, matchImpl, ListBuffer[Trick](), players_in, firstRound = firstRound)
  }

  private var current_trick: Option[Trick] = None

  def get_current_trick(): Trick = {
    current_trick.getOrElse(create_trick())
  }

  def get_tricks(): List[Trick] = tricklist.toList
  
  def create_trick(): Trick = {
    val trick = new Trick(this)
    current_trick = Some(trick)
    trick
  }

  def isOver: Boolean = {
    players_in.map(_.currentHand()).count(_.get.cards.isEmpty) == players_in.size
  }
  
  def dogNeedsToPlay: Boolean = {
    players_in.filter(!_.doglife).map(_.currentHand()).exists(_.get.cards.isEmpty)
  }

  def finalizeRound(force: Boolean = false): (Player, Round) = {
    if(!force && tricklist.isEmpty)
      throw new IllegalStateException("No tricks played in this round")
    if(!force && !isOver)
      throw new IllegalStateException("Not all tricks were played in this round")
    val tricksMapped = tricklist
      .map(t => t.winner)
      .groupBy(identity).map((p, l) => (p, l.size)) //l.size = Anzahl gewonnener Tricks
    val winners = tricksMapped
      .filter((p, i) => i == tricksMapped.values.max)
      .keys

    var playersOut = firstRound
      ? List()
      |: players_in.filter(!tricksMapped.contains(_))

    if(playersOut.nonEmpty && !matchImpl.dogLife) {
      matchImpl.dogLife = true
      playersOut.foreach(p => p.doglife = true)
      playersOut = List()
    }

    tricksMapped.keys.foreach(p => {p.doglife = false})

    val winner = (winners.size == 1)
      ? winners.head
      |: KnockOutWhist.matchControl.playerControl.determineWinnerTie(winners.toList)

    val finalRound = Round(trumpSuit, matchImpl, tricklist, players_in, playersOut, winner, firstRound)
    matchImpl.roundlist += finalRound
    (winner, finalRound)
  }
      
  def remainingPlayers(): List[Player] = {
    if (players_out == null) {
      return players_in
    }
    players_in.filter(!players_out.contains(_))
  }
  override def toString: String = {
    s"$trumpSuit, $tricklist, $players_in, $players_out, $winner, $firstRound"
  }
  
  
}
