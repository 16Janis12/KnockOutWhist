package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.events.GLOBAL_STATUS.SHOW_FINISHED_MATCH
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.directional.RequestPlayersEvent
import de.knockoutwhist.events.{ShowGlobalStatus, ShowPlayerStatus, ShowRoundStatus}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.{CreateTrickCommand, EnterPlayersCommand, PlayerPlayCommand, RoundCreateCommand}
import de.knockoutwhist.utils.QueueIterator

object MainLogic {

  def startMatch(): Unit = {
    val players = ControlHandler.invoke(RequestPlayersEvent())
    UndoManager.doStep(EnterPlayersCommand(players))
  }

  def controlMatch(matchImpl: Match): Unit = {
    if(MatchLogic.isOver(matchImpl)) {
      ControlHandler.invoke(ShowGlobalStatus(SHOW_FINISHED_MATCH, RoundLogic.remainingPlayers(matchImpl.roundlist.last).head))
    } else {
      UndoManager.doStep(RoundCreateCommand(matchImpl))
    }
  }

  def controlRound(matchImpl: Match, round: Round): Unit = {
    if(!RoundLogic.isOver(round)) {
      UndoManager.doStep(CreateTrickCommand(matchImpl, round))
      return
    }
    val result = RoundLogic.finalizeRound(RoundLogic.smashResults(round), matchImpl)
    if(result._3.size == 1) {
      endRound(result._1, result._2, result._3.head, result._4)
    } else {
      PlayerLogic.preSelect(result._3, result._1, result._2, result._4)
    }
  }

  def endRound(matchImpl: Match, round: Round, winner: AbstractPlayer, playersOut: List[AbstractPlayer]): Unit = {
    val finalRound = Round(round.trumpSuit, round.tricklist, round.playersin, playersOut, round.startingPlayer, winner, firstRound = round.firstRound)
    val newMatch = matchImpl.addRound(finalRound)
    ControlHandler.invoke(ShowRoundStatus(WON_ROUND, finalRound, winner))
    ControlHandler.invoke(DelayEvent(5000L))
    if (finalRound.playersout.nonEmpty) {
      ControlHandler.invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
    }
    controlMatch(newMatch)
  }

  def controlTrick(matchImpl: Match, round: Round, trick: Trick, currentIndex: Int = 0): Unit = {
    if(currentIndex < round.playersin.size) {
      val player = round.playerQueue.nextPlayer()
      ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
      UndoManager.doStep(PlayerPlayCommand(matchImpl, round, trick, player, currentIndex))
    }else {
      val result = TrickLogic.wonTrick(trick, round)
      val newRound = round.addTrick(result._2)
      ControlHandler.invoke(ShowCurrentTrickEvent(newRound, result._2))
      ControlHandler.invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, result._1))
      newRound.playerQueue.resetAndSetStart(result._1)
      ControlHandler.invoke(DelayEvent(3000L))
      controlRound(matchImpl, newRound)
    }
  }
  
  def controlPlayer(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
    if (!player.doglife) {
      val rightCard = TrickLogic.controlSuitplayed(trick, player)
      val newPlayer = player.removeCard(rightCard)
      val newTrick = playCard(trick, rightCard, newPlayer)
      val newRound = round.updatePlayersIn(round.playersin.updated(round.playersin.indexOf(player), newPlayer))
      val newMatch = matchImpl.updatePlayers(matchImpl.totalplayers.updated(matchImpl.totalplayers.indexOf(player), newPlayer))
      controlTrick(newMatch, newRound, newTrick, currentIndex+1)
    } else if (player.currentHand().exists(_.cards.nonEmpty)) {
      val card = PlayerControl.dogplayCard(player, round, trick)
      if (card.isEmpty) {
        ControlHandler.invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
        controlTrick(matchImpl, round, trick, currentIndex+1)
      } else {
        val newPlayer = player.removeCard(card.get)
        val newTrick = playCard(trick, card.get, newPlayer)
        val newRound = round.updatePlayersIn(round.playersin.updated(round.playersin.indexOf(player), newPlayer))
        val newMatch = matchImpl.updatePlayers(matchImpl.totalplayers.updated(matchImpl.totalplayers.indexOf(player), newPlayer))
        controlTrick(newMatch, newRound, newTrick, currentIndex+1)
      }
    }else {
      controlTrick(matchImpl, round, trick, currentIndex+1)
    }
  }

  def playCard(trick: Trick, card: Card, player: AbstractPlayer): Trick = {
    if (trick.firstCard.isEmpty) {
      trick.setfirstcard(card).addCard(card, player)
    } else {
      trick.addCard(card, player)
    }
  }
  
}
