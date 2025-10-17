package de.knockoutwhist.control.sublogic

import de.knockoutwhist.control.SnapshottingGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundResult
import de.knockoutwhist.player.AbstractPlayer

trait PlayerTieLogic extends SnapshottingGameLogic {

  def handleTie(roundResult: RoundResult): Unit
  def handleNextTieBreakerPlayer(): Unit
  def receivedTieBreakerCard(number: Int): Unit
  def highestAllowedNumber(): Int

}
