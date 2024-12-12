package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.events.PLAYER_STATUS.SHOW_TURN
import de.knockoutwhist.events.{ShowGlobalStatus, ShowPlayerStatus}
import de.knockoutwhist.events.directional.RequestPlayersEvent
import de.knockoutwhist.events.ui.GameState.{INGAME, MAIN_MENU}
import de.knockoutwhist.events.ui.GameStateUpdateEvent
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

  private var currentRoot: Parent = uninitialized

  override def listen(event: SimpleEvent): Unit = {
    while (!platformReady) {
      Thread.sleep(100)
    }
    Platform.runLater {
      event match {
        case event: RequestPlayersEvent => 
          MainMenu.createPlayeramountmenu()
        case event: GameStateUpdateEvent => 
          if (event.gameState == INGAME) {
            Game.createGame()
          } else if (event.gameState == MAIN_MENU) {
            MainMenu.createMainMenu
          }
        case event: ShowPlayerStatus =>
          event.status match
            case SHOW_TURN =>
              UIData.currentPlayer = event.player
        case event: SimpleEvent =>
          println(s"Event ${event.id} not handled")
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
}

object GUIThread extends CustomThread {

  setName("GUIThread")

  override def instance: CustomThread = GUIThread

  override def run(): Unit = {
    GUIMain.main(new Array[String](_length = 0))
  }
  
}
