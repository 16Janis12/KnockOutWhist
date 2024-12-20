package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, Playeractrcomponent}
import de.knockoutwhist.events.PLAYER_STATUS.*
import de.knockoutwhist.events.ShowPlayerStatus
import de.knockoutwhist.events.cards.RenderHandEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

object PlayerControl extends Playeractrcomponent {
  
  
  def playCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(500))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_PLAY_CARD, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, true))
    player.handlePlayCard(player.currentHand().get, matchImpl, round, trick, currentIndex)
  }
  
  def dogplayCard(matchImpl: Match, player: AbstractPlayer, round: Round, trick: Trick, currentIndex: Int): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TURN, player))
    ControlHandler.invoke(DelayEvent(2000))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_DOG_PLAY_CARD, player, KnockOutWhist.config.roundlogcomponent.dogNeedsToPlay(round)))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handleDogPlayCard(player.currentHand().get, matchImpl, round, trick, currentIndex, KnockOutWhist.config.roundlogcomponent.dogNeedsToPlay(round))
  }
  
  def pickNextTrumpsuit(matchImpl: Match, remaining_players: List[AbstractPlayer], firstRound: Boolean, player: AbstractPlayer): Unit = {
    ControlHandler.invoke(ShowPlayerStatus(SHOW_TRUMPSUIT_OPTIONS, player))
    ControlHandler.invoke(RenderHandEvent(player.currentHand().get, false))
    player.handlePickTrumpsuit(matchImpl, remaining_players, firstRound)
  }
}
