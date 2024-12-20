package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.*
import de.knockoutwhist.events.GLOBAL_STATUS.SHOW_FINISHED_MATCH
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_WON_PLAYER_TRICK
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, WON_ROUND}
import de.knockoutwhist.events.directional.RequestPlayersEvent
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.ui.GameState.MAIN_MENU
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{ShowGlobalStatus, ShowPlayerStatus, ShowRoundStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.EnterPlayersCommand
import de.knockoutwhist.utils.Implicits.*

object MainLogic extends Maincomponent {

  def startMatch(): Unit = {
    ControlHandler.invoke(RequestPlayersEvent())
  }
  
  def enteredPlayers(players: List[AbstractPlayer]): Unit = {
    UndoManager.doStep(EnterPlayersCommand(players))
  }

  def controlMatch(matchImpl: Match): Unit = {
    if(KnockOutWhist.config.matchcomponent.isOver(matchImpl)) {
      ControlHandler.invoke(ShowGlobalStatus(SHOW_FINISHED_MATCH, KnockOutWhist.config.roundlogcomponent.remainingPlayers(matchImpl.roundlist.last).head))
      ControlHandler.invoke(GameStateUpdateEvent(MAIN_MENU))
    } else {
      val remainingPlayer = matchImpl.roundlist.isEmpty ? matchImpl.totalplayers |: KnockOutWhist.config.roundlogcomponent.remainingPlayers(matchImpl.roundlist.last)
      val newMatch = KnockOutWhist.config.roundlogcomponent.provideCards(matchImpl, remainingPlayer)
      KnockOutWhist.config.playerlogcomponent.trumpsuitStep(newMatch._1, newMatch._2)
    }
  }

  def controlRound(matchImpl: Match, round: Round): Unit = {
    if(!KnockOutWhist.config.roundlogcomponent.isOver(round)) {
      val trick = Trick()
      controlTrick(matchImpl, round, trick)
      return
    }
    val result = KnockOutWhist.config.roundlogcomponent.finalizeRound(KnockOutWhist.config.roundlogcomponent.smashResults(round), matchImpl)
    if(result._3.size == 1) {
      endRound(result._1, result._2, result._3.head, result._4)
    } else {
      KnockOutWhist.config.playerlogcomponent.preSelect(result._3, result._1, result._2, result._4)
    }
  }

  def endRound(matchImpl: Match, round: Round, winner: AbstractPlayer, playersOut: List[AbstractPlayer]): Unit = {
    val finalRound = Round(round.trumpSuit, round.tricklist, round.playersin, playersOut, round.startingPlayer, winner, firstRound = round.firstRound)
    val newMatch = matchImpl.addRound(finalRound)
    ControlHandler.invoke(ShowRoundStatus(WON_ROUND, finalRound, winner))
    ControlHandler.invoke(DelayEvent(2000L))
    if (finalRound.playersout.nonEmpty) {
      ControlHandler.invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
    }
    controlMatch(newMatch)
  }

  def controlTrick(matchImpl: Match, round: Round, trick: Trick, currentIndex: Int = 0): Unit = {
    if(currentIndex < round.playersin.size) {
      val player = round.playerQueue.nextPlayer()
      //ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
      controlPlayer(matchImpl, round, trick, player, currentIndex)
    }else {
      val result = KnockOutWhist.config.trickcomponent.wonTrick(trick, round)
      val newRound = round.addTrick(result._2)
      ControlHandler.invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, result._1, result._2))
      newRound.playerQueue.resetAndSetStart(result._1)
      ControlHandler.invoke(DelayEvent(1000L))
      controlRound(matchImpl, newRound)
    }
  }
  
  def controlPlayer(matchImpl: Match, round: Round, trick: Trick, player: AbstractPlayer, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
    if (!player.doglife) {
      KnockOutWhist.config.playeractrcomponent.playCard(matchImpl, player, round, trick, currentIndex)
    } else if (player.currentHand().exists(_.cards.nonEmpty)) {
      KnockOutWhist.config.playeractrcomponent.dogplayCard(matchImpl, player, round, trick, currentIndex)
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
