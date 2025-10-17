package de.knockoutwhist.undo.commands

import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.sublogic.PlayerTieLogic
import de.knockoutwhist.control.{ControlThread, GameLogic, LogicSnapshot}
import de.knockoutwhist.undo.Command

case class PlayCardCommand[
  GL <: GameLogic,
  PT <: PlayerTieLogic
] (
         gameLogicSnapshot: LogicSnapshot[? <: GL],
         playerTieLogicSnapShot: LogicSnapshot[? <: PT],
         card: Card
                                   ) extends Command {

  override def doStep(gameLogic: GameLogic): Unit = {
    ControlThread.runLater {
      gameLogic.playerInputLogic.receivedCard(card)
    }

  }

  override def undoStep(gameLogic: GameLogic): Unit = {
    val glSnapshot = gameLogicSnapshot.asInstanceOf[LogicSnapshot[GL]]
    val ptlSnapshot = playerTieLogicSnapShot.asInstanceOf[LogicSnapshot[PT]]
    glSnapshot.restore(gameLogic.asInstanceOf[GL])
    ptlSnapshot.restore(gameLogic.playerTieLogic.asInstanceOf[PT])
    ControlThread.runLater {
      gameLogic.controlPlayerPlay()
    }
  }
}
