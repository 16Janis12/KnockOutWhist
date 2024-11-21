package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.control.MatchControl.playerQueue
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.utils.Implicits.*

import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.ShowRoundStatus
import de.knockoutwhist.events.util.DelayEvent

object RoundControl {

  def isOver(round: Round): Boolean = {
    round.playersin.map(_.currentHand()).count(_.get.cards.isEmpty) == round.playersin.size
  }

  def dogNeedsToPlay(round: Round): Boolean = {
    round.playersin.filter(!_.doglife).map(_.currentHand()).exists(_.get.cards.isEmpty)
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
      |: round.playersin.filter(!tricksMapped.contains(_))

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

    val finalRound = Round(round.trumpSuit, matchImpl, round.tricklist, round.playersin, playersOut, winner, round.firstRound)
    matchImpl.roundlist += finalRound
    (winner, finalRound)
  }

  def remainingPlayers(round: Round): List[Player] = {
    if (round.playersout == null) {
      return round.playersin
    }
    round.playersin.filter(!round.playersout.contains(_))
  }

  def createround(matchImpl: Match): Round = {
    val remainingPlayer = matchImpl.roundlist.isEmpty ? matchImpl.totalplayers |: RoundControl.remainingPlayers(matchImpl.roundlist.last)
    provideCards(matchImpl, remainingPlayer)
    if (matchImpl.roundlist.isEmpty) {
      val randomTrumpsuit = CardManager.nextCard().suit
      matchImpl.current_round = Some(new Round(randomTrumpsuit, matchImpl, remainingPlayer, true))
    } else {
      val winner = matchImpl.roundlist.last.winner
      val trumpsuit = PlayerControl.pickNextTrumpsuit(winner)

      matchImpl.current_round = Some(new Round(trumpsuit, matchImpl, remainingPlayer, false))
    }
    matchImpl.numberofcards -= 1
    matchImpl.current_round.get
  }

  def nextRound(matchImpl: Match): Round = {
    if (MatchControl.isOver(matchImpl)) {
      return null
    }
    createround(matchImpl)
  }


  def controlRound(matchImpl: Match): Round = {
    val roundImpl = nextRound(matchImpl)
    ControlHandler.invoke(ShowRoundStatus(SHOW_START_ROUND, roundImpl))
    while (!RoundControl.isOver(roundImpl)) {
      TrickControl.controlTrick(roundImpl)
    }
    val (roundWinner, finalRound) = RoundControl.finalizeRound(roundImpl, matchImpl)
    ControlHandler.invoke(ShowRoundStatus(WON_ROUND, finalRound, roundWinner))
    ControlHandler.invoke(DelayEvent(5000L))
    if (finalRound.playersout.nonEmpty) {
      ControlHandler.invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
      finalRound.playersout.foreach(p => {
        playerQueue.remove(p)
      })
    }
    playerQueue.resetAndSetStart(roundWinner)
    finalRound
  }


  private def provideCards(matchImpl: Match, players: List[Player]): Int = {
    if (!KnockOutWhist.debugmode) CardManager.shuffleAndReset()
    var hands = 0
    for (player <- players) {
      if (!player.doglife) {
        player.provideHand(CardManager.createHand(matchImpl.numberofcards))
      } else {
        player.provideHand(CardManager.createHand(1))
      }
      hands += 1
    }
    hands
  }
}
