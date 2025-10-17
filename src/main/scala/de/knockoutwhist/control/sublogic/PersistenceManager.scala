package de.knockoutwhist.control.sublogic

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.GameLogic
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}

import scala.Unit

trait PersistenceManager {
    
    def gameLogic: GameLogic
    def update(methodEntryPoint: MethodEntryPoint): MatchSnapshot
    
    def saveFile(path: String): Unit
    def loadFile(path: String): Unit

    def canLoadfile(path: String): Boolean = {
        val file = new java.io.File(path + "." + KnockOutWhist.config.fileFormatter.fileEnding)
        file.exists() && file.isFile && file.canRead
    }
    
}
