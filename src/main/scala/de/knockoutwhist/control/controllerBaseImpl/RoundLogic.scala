package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.{ControlHandler, Roundlogcomponent}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.Implicits.*

import scala.collection.mutable.ListBuffer

object RoundLogic extends Roundlogcomponent{

  def isOver(round: Round): Boolean = {
    round.playersin.map(_.currentHand()).count(_.get.cards.isEmpty) == round.playersin.size
  }

  def dogNeedsToPlay(round: Round): Boolean = {
    round.playersin.filter(!_.doglife).map(_.currentHand()).exists(_.get.cards.isEmpty)
  }

  def finalizeRound(round: Round, matchImpl: Match, force: Boolean = false): (Match, Round, List[AbstractPlayer], List[AbstractPlayer]) = {
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

    var newMatch = matchImpl
    var newRound = round
    if (playersOut.nonEmpty && !matchImpl.dogLife) {
      newMatch = matchImpl.setDogLife()

      val playersUpdated = ListBuffer[AbstractPlayer]()
      playersUpdated ++= tricksMapped.keys
      playersOut.foreach(p => {
        playersUpdated += p.setDogLife()
      })
      newMatch = newMatch.updatePlayers(playersUpdated.toList)
      newRound = newRound.updatePlayersIn(playersUpdated.toList)
      playersOut = List()
    }
    (newMatch, newRound, winners.toList, playersOut)
  }

  def remainingPlayers(round: Round): List[AbstractPlayer] = {
    if (round.playersout == null) {
      return round.playersin
    }
    round.playersin.filter(!round.playersout.contains(_))
  }

  def provideCards(matchImpl: Match, players: List[AbstractPlayer]): (Match,List[AbstractPlayer]) = {
    if (!KnockOutWhist.debugmode) KnockOutWhist.config.cardManager.shuffleAndReset()
    val listbuff = new ListBuffer[AbstractPlayer]()
    for (player <- players) {
      if (!player.doglife) {
        val newPlayer = player.provideHand(KnockOutWhist.config.cardManager.createHand(matchImpl.numberofcards))
        listbuff.addOne(newPlayer)
      } else {
        val newPlayer = player.provideHand(KnockOutWhist.config.cardManager.createHand(1))
        listbuff.addOne(newPlayer)
      }
    }
    val matchResult = matchImpl.totalplayers.appendedAll(listbuff.toList).filter(!players.contains(_))
    (matchImpl.updatePlayers(matchResult), listbuff.toList)
  }

  def smashResults(round: Round): Round = {
    val correctPlayers = round.playersin.groupMapReduce(_.name)(identity)((a, b) => a)
    val newTricks = round.tricklist.map(t => Trick(t.cards, correctPlayers.getOrElse(t.winner.name, t.winner), t.finished, t.firstCard))
    Round(round.trumpSuit, newTricks, round.playersin, round.playersout, round.startingPlayer, round.winner, round.firstRound)
  }
}
