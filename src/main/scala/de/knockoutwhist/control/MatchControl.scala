package de.knockoutwhist.control

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager}
import de.knockoutwhist.events.*
import de.knockoutwhist.events.ERROR_STATUS.{IDENTICAL_NAMES, INVALID_NAME_FORMAT, INVALID_NUMBER_OF_PLAYERS, WRONG_CARD}
import de.knockoutwhist.events.GLOBAL_STATUS.*
import de.knockoutwhist.events.MATCH_STATUS.SHOW_FINISHED
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_NOT_PLAYED, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.{PLAYERS_OUT, SHOW_START_ROUND, WON_ROUND}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.util.DelayEvent
import de.knockoutwhist.player.Player
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*

import scala.compiletime.uninitialized
import scala.io.StdIn
import scala.util.Random

object MatchControl {

  private[control] var playerQueue: CustomPlayerQueue[Player] = uninitialized

  def startMatch(): Player = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_START_MATCH))
    val players = enterPlayers()
    playerQueue = CustomPlayerQueue[Player](players, Random.nextInt(players.length))
    controlMatch()
  }

  def enterPlayers(): Array[Player] = {
    ControlHandler.invoke(ShowGlobalStatus(SHOW_TYPE_PLAYERS))
    val names = StdIn.readLine().split(",")
    if (names.length < 2) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NUMBER_OF_PLAYERS))
      return enterPlayers()
    }
    if (names.distinct.length != names.length) {
      ControlHandler.invoke(ShowErrorStatus(IDENTICAL_NAMES))
      return enterPlayers()
    }
    if (names.count(_.trim.isBlank) > 0 || names.count(_.trim.length <= 2) > 0 || names.count(_.trim.length > 10) > 0) {
      ControlHandler.invoke(ShowErrorStatus(INVALID_NAME_FORMAT))
      return enterPlayers()
    }
    names.map(s => Player(s))
  }

  def controlMatch(): Player = {
    val matchImpl = Match(playerQueue.toList)
    while (!isOver(matchImpl)) {
      val roundImpl = controlRound(matchImpl)
    }
    val winner = finalizeMatch(matchImpl)
    val playerwinner = winner.name
    ControlHandler.invoke(ShowMatchStatus(SHOW_FINISHED, matchImpl, playerwinner))
    winner
  }

  def controlRound(matchImpl: Match): Round = {
    val roundImpl = nextRound(matchImpl)
    ControlHandler.invoke(ShowRoundStatus(SHOW_START_ROUND, roundImpl))
    while (!RoundControl.isOver(roundImpl)) {
      controlTrick(roundImpl)
    }
    val (roundWinner, finalRound) = RoundControl.finalizeRound(roundImpl, matchImpl)
    ControlHandler.invoke(ShowRoundStatus(WON_ROUND, finalRound, roundWinner))
    if (!KnockOutWhist.DEBUG_MODE) ControlHandler.invoke(DelayEvent(5000L))
    if (finalRound.players_out.nonEmpty) {
      ControlHandler.invoke(ShowRoundStatus(PLAYERS_OUT, finalRound))
      finalRound.players_out.foreach(p => {
        playerQueue.remove(p)
      })
    }
    playerQueue.resetAndSetStart(roundWinner)
    finalRound
  }

  def controlTrick(round: Round): Trick = {
    val trick = nextTrick(round)
    for (player <- playerQueue) {
      ControlHandler.invoke(ShowCurrentTrickEvent(round, trick))
      if (!player.doglife) {
        val rightCard = controlSuitplayed(trick, player)
        player.removeCard(rightCard)
        TrickControl.playCard(trick, round, rightCard, player)
      } else if (player.currentHand().exists(_.cards.nonEmpty)) {
        val card = PlayerControl.dogplayCard(player, round)
        if (card.isEmpty) {
          ControlHandler.invoke(ShowPlayerStatus(SHOW_NOT_PLAYED, player))
        } else {
          player.removeCard(card.get)
          TrickControl.playCard(trick, round, card.get, player)
        }
      }
    }
    val (winner, finalTrick) = TrickControl.wonTrick(trick, round)
    ControlHandler.invoke(ShowCurrentTrickEvent(round, finalTrick))
    ControlHandler.invoke(ShowPlayerStatus(SHOW_WON_PLAYER_TRICK, winner))
    playerQueue.resetAndSetStart(winner)
    if (!KnockOutWhist.DEBUG_MODE) ControlHandler.invoke(DelayEvent(3000L))
    finalTrick
  }

  private[control] def controlSuitplayed(trick: Trick, player: Player): Card = {
    var card = PlayerControl.playCard(player)
    if (trick.get_first_card().isDefined) {
      val firstCard = trick.get_first_card().get
      while (!(firstCard.suit == card.suit)) {
        var hasSuit = false
        for (cardInHand <- player.currentHand().get.cards) {
          if (cardInHand.suit == firstCard.suit) {
            hasSuit = true
          }
        }
        if (!hasSuit) {
          return card
        } else {
          ControlHandler.invoke(ShowErrorStatus(WRONG_CARD, firstCard))
          card = PlayerControl.playCard(player)
        }
      }
    }
    card
  }

  def nextRound(matchImpl: Match): Round = {
    if (MatchControl.isOver(matchImpl)) {
      return null
    }
    create_round(matchImpl)
  }

  def nextTrick(roundImpl: Round): Trick = {
    if (RoundControl.isOver(roundImpl)) {
      return null
    }
    RoundControl.create_trick(roundImpl)
  }

  def create_round(matchImpl: Match): Round = {
    val remainingPlayer = matchImpl.roundlist.isEmpty ? matchImpl.totalplayers |: RoundControl.remainingPlayers(matchImpl.roundlist.last)
    provideCards(matchImpl,remainingPlayer)
    if (matchImpl.roundlist.isEmpty) {
      val random_trumpsuit = CardManager.nextCard().suit
      matchImpl.current_round = Some(new Round(random_trumpsuit, matchImpl, remainingPlayer, true))
    } else {
      val winner = matchImpl.roundlist.last.winner
      val trumpsuit = PlayerControl.pickNextTrumpsuit(winner)

      matchImpl.current_round = Some(new Round(trumpsuit, matchImpl, remainingPlayer, false))
    }
    matchImpl.number_of_cards -= 1
    matchImpl.current_round.get
  }

  def isOver(matchImpl: Match): Boolean = {
    if (matchImpl.roundlist.isEmpty) {
      false
    } else {
      RoundControl.remainingPlayers(matchImpl.roundlist.last).size == 1
    }
  }

  private def provideCards(matchImpl: Match, players: List[Player]): Int = {
    if (!KnockOutWhist.DEBUG_MODE) CardManager.shuffleAndReset()
    var hands = 0
    for (player <- players) {
      if (!player.doglife) {
        player.provideHand(CardManager.createHand(matchImpl.number_of_cards))
      } else {
        player.provideHand(CardManager.createHand(1))
      }
      hands += 1
    }
    hands
  }

  def finalizeMatch(matchImpl: Match): Player = {
    if (!isOver(matchImpl)) {
      throw new IllegalStateException("Match is not over yet.")
    }
    RoundControl.remainingPlayers(matchImpl.roundlist.last).head
  }

}
