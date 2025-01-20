package de.knockoutwhist.persistence

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.ControlHandler
import de.knockoutwhist.events.ui.{GameState, GameStateUpdateEvent}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}

import scala.Unit

trait PersistenceManager extends EventListener {
    
    def updateGameState(gameState: GameState): MatchSnapshot
    def updateMatch(matchImpl: Match): MatchSnapshot
    def updateRound(round: Round): MatchSnapshot
    def updateTrick(trick: Trick): MatchSnapshot
    def updateCurrentIndex(currentIndex: Int): MatchSnapshot
    def updateMethodEntryPoint(methodEntryPoint: MethodEntryPoint): MatchSnapshot
    
    def saveFile(path: String): Unit
    def loadFile(path: String): Unit

    def canLoadfile(path: String): Boolean = {
        val file = new java.io.File(path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
        file.exists() && file.isFile && file.canRead
    }
    
    override def listen(event: SimpleEvent): Unit = {
        event match {
            case event: GameStateUpdateEvent =>
                updateGameState(event.gameState)
            case _ =>
        }
    }
    
    def loadManager(): Unit = {
        ControlHandler.addListener(this)
    }
    
}
