package de.knockoutwhist.control

import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.control.sublogic.{PersistenceManager, PlayerInputLogic, PlayerTieLogic, UndoManager}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.events.EventHandler

trait GameLogic extends EventHandler with SnapshottingGameLogic  {

  def createSession(): Unit
  def endSession(): Unit
  def createMatch(players: List[AbstractPlayer]): Match
  def controlMatch(): Unit
  def controlRound(): Unit
  def endRound(winner: AbstractPlayer, roundResult: RoundResult): Match
  def controlTrick(): Unit
  def endTrick(): Round
  def controlPlayerPlay(): Unit
  def providePlayersWithCards(): Unit
  
  def playerInputLogic: PlayerInputLogic
  def playerTieLogic: PlayerTieLogic
  def undoManager: UndoManager
  def persistenceManager: PersistenceManager

  def getCurrentState: GameState
  def getCurrentMatch: Option[Match]
  def getCurrentRound: Option[Round]
  def getCurrentTrick: Option[Trick]
  def getCurrentPlayer: Option[AbstractPlayer]
  def getPlayerQueue: Option[CustomPlayerQueue[AbstractPlayer]]
  
}
