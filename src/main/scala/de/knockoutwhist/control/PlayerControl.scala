package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, Suit}
import de.knockoutwhist.events.ERROR_STATUS.{INVALID_INPUT, INVALID_NUMBER, NOT_A_NUMBER}
import de.knockoutwhist.events.GLOBAL_STATUS.{SHOW_TIE, SHOW_TIE_TIE, SHOW_TIE_WINNER}
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.cards.{RenderHandEvent, ShowTieCardsEvent}
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.events.{ShowErrorStatus, ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

object PlayerControl {
  
  
  def playCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_PLAY_CARD, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, true))
    player.handlePlayCard(player.currentHand().get, matchImpl, round, trick, currentIndex)
  }
  
  def dogplayCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(3000L))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, RoundLogic.dogNeedsToPlay(round)))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handleDogPlayCard(player.currentHand().get, matchImpl, round, trick, currentIndex, RoundLogic.dogNeedsToPlay(round))
  }
  
  def pickNextTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean, player: AbstractPlayer): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handlePickTrumpsuit(matchImpl, remaining_players, firstRound)
  }
}
