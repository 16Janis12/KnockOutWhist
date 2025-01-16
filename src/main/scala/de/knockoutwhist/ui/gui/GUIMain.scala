package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.events.PLAYER_STATUS.{SHOW_TURN, SHOW_WON_PLAYER_TRICK}
import de.knockoutwhist.events.ROUND_STATUS.WON_ROUND
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestPickTrumpsuitEvent}
import de.knockoutwhist.events.round.ShowCurrentTrickEvent
import de.knockoutwhist.events.ui.GameState.{INGAME, MAIN_MENU, NO_SET, PLAYERS, TRUMPSUIT}
import de.knockoutwhist.events.ui.{GameState, GameStateUpdateEvent}
import de.knockoutwhist.events.{ShowGlobalStatus, ShowPlayerStatus, ShowRoundStatus}
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.CustomThread
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}
import javafx.application as jfxa
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Parent, Scene}

import scala.compiletime.uninitialized

object GUIMain extends JFXApp3 with EventListener with UI {

  private var platformReady: Boolean = false
  private var internState: GameState = NO_SET
  private var currentRoot: Parent = uninitialized

  override def listen(event: SimpleEvent): Unit = {
    while (!platformReady) {
      Thread.sleep(100)
    }
    Platform.runLater {
      event match {
        case event: GameStateUpdateEvent =>
          if (internState != event.gameState) {
            internState = event.gameState
            if (event.gameState == INGAME || event.gameState == TRUMPSUIT) {
              Game.createGame()
            } else if (event.gameState == MAIN_MENU) {
              MainMenu.createMainMenu
            } else if (event.gameState == PLAYERS) {
              MainMenu.createPlayeramountmenu()
            }
          }
        case event: ShowPlayerStatus =>
          event.status match
            case SHOW_TURN =>
              Game.updateStatus(event.player)
              Game.updatePlayerCards(event.player.hand.get)
            case SHOW_WON_PLAYER_TRICK =>
              Game.showFinishedTrick(event)
            case _ =>
        case event: ShowCurrentTrickEvent =>
          Game.updatePlayedCards(event.trick)
        case event: ShowRoundStatus =>
          event.status match
            case WON_ROUND =>
              Game.showWon(event.currentRound)
            case _ =>
        case event: RequestCardEvent =>
          Game.requestCard = Some(event)
          Game.updateNextPlayer(event.round.playerQueue, event.currentIndex)
          Game.updateTrumpSuit(event.round.trumpSuit)
          if(event.trick.firstCard.isDefined) Game.updateFirstCard(event.trick.firstCard.get)
          else Game.resetFirstCard()
        case event: RequestPickTrumpsuitEvent => 
          PickTrumsuit.showPickTrumpsuit(event)
      }
    }
  }

  override def initial: Boolean = {
    GUIThread.start()
    true
  }

  override def start(): Unit = {
    currentRoot = MainMenu.current_root
    val cont = ObjectProperty(currentRoot)
    JFXApp3.userAgentStylesheet_=(new PrimerDark().getUserAgentStylesheet)
    stage = new PrimaryStage {
      width = 800
      height = 600
      title = "Knockout Whist"
      scene = new Scene {
        root = currentRoot
        cont.onChange(Platform.runLater {
          root = currentRoot
        })
      }
    }
    stage.show()
    platformReady = true
  }

  override def stopApp(): Unit = {
    System.exit(0)
  }

}

object GUIThread extends CustomThread {

  setName("GUIThread")

  override def instance: CustomThread = GUIThread

  override def run(): Unit = {
    GUIMain.main(new Array[String](_length = 0))
  }
  
}
