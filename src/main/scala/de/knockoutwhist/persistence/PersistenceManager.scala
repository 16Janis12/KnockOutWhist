package de.knockoutwhist.persistence

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
