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
    def updateCurrentPlayer(currentPlayer: AbstractPlayer): MatchSnapshot
    def updateCurrentIndex(currentIndex: Int): MatchSnapshot
    def updateMethodEntryPoint(methodEntryPoint: MethodEntryPoint): MatchSnapshot
    
    override def listen(event: SimpleEvent): Unit = {
        event match {
            case event: GameStateUpdateEvent =>
                updateGameState(event.gameState)
        }
    }
    
    def loadManager(): Unit = {
        ControlHandler.addListener(this)
    }
    
}
