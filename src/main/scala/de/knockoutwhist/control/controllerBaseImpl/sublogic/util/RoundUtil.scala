package de.knockoutwhist.control.controllerBaseImpl.sublogic.util

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

object RoundUtil {
  
  def createRound(trumpSuit: Suit): Round = {
    Round(trumpSuit, false)
  }

  def finishRound(round: Round, matchImpl: Match): RoundResult = {
    val tricksMapped = round.tricklist
      .map(t => t.winner)
      .filter(t => t.isDefined)
      .map(t => t.get)
      .groupBy(identity).map((p, l) => (p, l.size))
    val maxTricks = if (tricksMapped.isEmpty) 0 else tricksMapped.values.max
    val winners = tricksMapped
      .filter((p, i) => i == maxTricks)
      .keys.toList
    val trickedPlayers = tricksMapped.keys.toList
    val notTrickedPlayers = matchImpl.playersIn.filterNot(trickedPlayers.contains)
    RoundResult(winners, trickedPlayers, notTrickedPlayers)
  }
  
  def roundEndSnapshot(winner: AbstractPlayer, round: Round): Round = {
    Round(
      round.trumpSuit,
      round.firstRound,
      round.tricklist,
      Some(winner)
    )
  }

}

case class RoundResult(winners: List[AbstractPlayer], tricked: List[AbstractPlayer], notTricked: List[AbstractPlayer]) {
  def isTie: Boolean = winners.size > 1
}
