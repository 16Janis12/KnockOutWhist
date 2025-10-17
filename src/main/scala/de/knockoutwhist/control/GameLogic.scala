package de.knockoutwhist.control

import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}

trait GameLogic extends SnapshottingGameLogic {

  def createSession(): Unit
  def createMatch(players: List[AbstractPlayer]): Match
  def controlMatch(): Unit
  def controlRound(): Unit
  def endRound(winner: AbstractPlayer, roundResult: RoundResult): Match
  def controlTrick(): Unit
  def endTrick(): Round
  def controlPlayerPlay(): Unit
  def providePlayersWithCards(): Unit
  
}
