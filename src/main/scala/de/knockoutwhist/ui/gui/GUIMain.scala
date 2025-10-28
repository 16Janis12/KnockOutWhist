package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.control.GameLogic
import de.knockoutwhist.control.GameState.*
import de.knockoutwhist.events.global.*
import de.knockoutwhist.events.global.tie.{TieShowPlayerCardsEvent, TieTieEvent, TieTurnEvent, TieWinningPlayersEvent}
import de.knockoutwhist.events.player.{RequestCardEvent, RequestTieChoiceEvent, RequestTrumpSuitEvent}
import de.knockoutwhist.events.util.ReloadAllEvent
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.CustomThread
import de.knockoutwhist.utils.events.{EventListener, SimpleEvent}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.application.{JFXApp3, Platform}
import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Parent, Scene}

import scala.compiletime.uninitialized

class GUIMain extends JFXApp3 with EventListener with UI {

  private var platformReady: Boolean = false
  private var currentRoot: Parent = uninitialized
  private var _logic: Option[GameLogic] = None

  //UIS
  private var _mainMenu: MainMenu = uninitialized
  private var _game: Game = uninitialized
  private var _tieMenu: TieMenu = uninitialized
  private var _pickTrumpsuit: PickTrumsuit = uninitialized
  private var _winnerScreen: WinnerScreen = uninitialized
  
  def mainMenu: MainMenu = _mainMenu
  
  def logic: Option[GameLogic] = _logic

  override def listen(event: SimpleEvent): Unit = {
    while (!platformReady) {
      Thread.sleep(100)
    }
    Platform.runLater {
      event match {
        case event: GameStateChangeEvent =>
          if (event.newState == InGame) {
            _game.createGame()
          } else if (event.newState == MainMenu) {
            _mainMenu.createMainMenu
          } else if (event.newState == Lobby) {
            _mainMenu.createPlayeramountmenu()
          } else if (event.newState == TieBreak) {
            _tieMenu.spawnTieMain()
          }
        case event: TieWinningPlayersEvent =>
          _tieMenu.updateWinnerLabel(event)

        case event: TieTieEvent =>
          _tieMenu.showTieAgain(event)
        case event: MatchEndEvent =>
          _winnerScreen.spawnWinnerScreen(event.winner)
        case event: TrickEndEvent =>
          _game.showFinishedTrick(event)
        case event: TieTurnEvent =>
          _tieMenu.updatePlayerLabel(event.player)
          _tieMenu.changeSlider(logic.get.playerTieLogic.highestAllowedNumber())
        case event: NewRoundEvent =>
          _game.updateTrumpSuit(logic.get.getCurrentRound.get.trumpSuit)
        case event: NewTrickEvent =>
          _game.resetFirstCard()
        case event: RoundEndEvent =>
          _game.showWon(event.winner, event.amountOfTricks)
        case event: TurnEvent =>
          _game.updateStatus(event.player)
          _game.updateNextPlayer(_logic.get.getPlayerQueue.get, _logic.get.getPlayerQueue.get.currentIndex)
        case event: TieShowPlayerCardsEvent =>
          val cards = logic.get.playerTieLogic.getSelectedCard
          _tieMenu.addCutCards(cards.map((p, c) => (p, c)).toList)
        case event: CardPlayedEvent =>
          _game.updatePlayedCards()
          if (event.trick.firstCard.isDefined)
            _game.updateFirstCard(event.trick.firstCard.get)
        case event: TrumpSelectedEvent =>
          _game.updateTrumpSuit(event.suit)
        case event: RequestCardEvent =>
          _game.updatePlayerCards(event.player)
        case event: RequestTieChoiceEvent =>
          _tieMenu.showNeccessary()
        case event: RequestTrumpSuitEvent =>
          _pickTrumpsuit.showPickTrumpsuit(event.player)
        case event: ReloadAllEvent =>
          if (_logic.isEmpty) throw new Exception("Logic is not initialized!")
          val logicImpl = _logic.get
          if (logicImpl.getCurrentState == MainMenu)
            _mainMenu.createMainMenu
          else if (logicImpl.getCurrentState == Lobby)
            _mainMenu.createPlayeramountmenu()
          else if (logicImpl.getCurrentState == InGame)
            _game.reloadAll()
          else if (logicImpl.getCurrentState == TieBreak)
            _tieMenu.reloadAll()
          else if (logicImpl.getCurrentState == SelectTrump)
            _pickTrumpsuit.reloadAll()
        case _ => None
      }
    }
  }

  override def initial(logic: GameLogic): Boolean = {
    this._logic = Some(logic)
    new GUIThread(this).start()
    true
  }

  override def start(): Unit = {
    _game = new Game(this)
    _mainMenu = new MainMenu(this)
    _tieMenu = new TieMenu(this)
    _pickTrumpsuit = new PickTrumsuit(this)
    _winnerScreen = new WinnerScreen(this)

    currentRoot = mainMenu.current_root
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

    _mainMenu.createMainMenu
    stage.show()
    platformReady = true
  }

  override def stopApp(): Unit = {
    System.exit(0)
  }

}

class GUIThread(gui: GUIMain) extends CustomThread {

  setName("GUIThread")

  override def instance: CustomThread = this

  override def run(): Unit = {
    gui.main(new Array[String](_length = 0))
  }
  
}
