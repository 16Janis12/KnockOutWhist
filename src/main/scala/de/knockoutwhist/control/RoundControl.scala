package de.knockoutwhist.control

import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.Implicits.*

object RoundControl {

  def create_trick(round: Round): Trick = {
    val trick = new Trick(round)
    round.set_current_trick(trick)
    trick
  }

  def isOver(round: Round): Boolean = {
    round.players_in.map(_.currentHand()).count(_.get.cards.isEmpty) == round.players_in.size
  }

  def dogNeedsToPlay(round: Round): Boolean = {
    round.players_in.filter(!_.doglife).map(_.currentHand()).exists(_.get.cards.isEmpty)
  }

  def finalizeRound(round: Round, matchImpl: Match,force: Boolean = false): (Player, Round) = {
    if (!force && round.tricklist.isEmpty)
      throw new IllegalStateException("No tricks played in this round")
    if (!force && !isOver(round))
      throw new IllegalStateException("Not all tricks were played in this round")
    val tricksMapped = round.tricklist
      .map(t => t.winner)
      .groupBy(identity).map((p, l) => (p, l.size)) //l.size = Anzahl gewonnener Tricks
    val winners = tricksMapped
      .filter((p, i) => i == tricksMapped.values.max)
      .keys

    var playersOut = round.firstRound
      ? List()
      |: round.players_in.filter(!tricksMapped.contains(_))

    if (playersOut.nonEmpty && !matchImpl.dogLife) {
      matchImpl.dogLife = true
      playersOut.foreach(p => p.doglife = true)
      playersOut = List()
    }

    tricksMapped.keys.foreach(p => {
      p.doglife = false
    })

    val winner = (winners.size == 1)
      ? winners.head
      |: PlayerControl.determineWinnerTie(winners.toList)

    val finalRound = Round(round.trumpSuit, matchImpl, round.tricklist, round.players_in, playersOut, winner, round.firstRound)
    matchImpl.roundlist += finalRound
    (winner, finalRound)
  }

  def remainingPlayers(round: Round): List[Player] = {
    if (round.players_out == null) {
      return round.players_in
    }
    round.players_in.filter(!round.players_out.contains(_))
  }

}
