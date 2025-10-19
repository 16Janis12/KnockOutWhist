package de.knockoutwhist.control.sublogic

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.SnapshottingGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.player.AbstractPlayer

trait PlayerTieLogic extends SnapshottingGameLogic {

  def handleTie(roundResult: RoundResult): Unit
  def handleNextTieBreakerPlayer(): Unit
  def currentTiePlayer(): AbstractPlayer
  def requestTieChoice(player: AbstractPlayer): Unit
  def receivedTieBreakerCard(number: Int): Unit
  def highestAllowedNumber(): Int

  def isWaitingForInput: Boolean

  def getRoundResult: Option[RoundResult]
  def getTiedPlayers: List[AbstractPlayer]
  def getTieBreakerIndex: Int
  def getLastNumber: Int
  def getSelectedCard: Map[AbstractPlayer, Card]
  
}
