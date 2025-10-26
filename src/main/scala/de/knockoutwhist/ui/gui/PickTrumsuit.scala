package de.knockoutwhist.ui.gui

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.undo.commands.SelectTrumpSuitCommand
import de.knockoutwhist.utils.gui.Animations
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, TopCenter}
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

class PickTrumsuit(gui: GUIMain) {

  private[ui] def reloadAll(): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Logic is not initialized!")
    val logic = gui.logic.get
    if (logic.getCurrentMatch.isEmpty) throw new IllegalStateException("No current match set")
    val matchImpl = logic.getCurrentMatch.get
    //Check if the last round had a winner
    val lastRound = matchImpl.roundlist.last
    if (lastRound.winner.isEmpty)
      throw new IllegalStateException("Last round had no winner")
    val lastWinner = lastRound.winner.get
    showPickTrumpsuit(lastWinner)
  }

  def showPickTrumpsuit(player: AbstractPlayer): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Game logic is not initialized in GUI")
    val logicImpl = gui.logic.get
    
    gui.mainMenu.changeChild(
      new StackPane {
        children = Seq(
          new VBox {
            alignment = TopCenter
            spacing = 10
            margin = Insets(20, 0, 0, 0)
            hgrow = Always
            children = Seq(
              new Label {
                alignment = TopCenter
                text = "Pick your trumpsuit"
                font = Font.font(30)
                margin = Insets(20, 0, 0, 0)
              },
              new HBox {
                alignment = BottomCenter
                spacing = 10
                margin = Insets(40, 0, 20, 0)
                children = Seq(
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AS.png")
                    fitWidth = 102
                    fitHeight = 150
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        logicImpl.undoManager.doStep(
                          SelectTrumpSuitCommand(
                            logicImpl.createSnapshot(),
                            logicImpl.playerTieLogic.createSnapshot(),
                            Suit.Spades
                          )
                        )
                      }

                    }
                  },
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AC.png")
                    fitWidth = 102
                    fitHeight = 150
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(this, Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        logicImpl.undoManager.doStep(
                          SelectTrumpSuitCommand(
                            logicImpl.createSnapshot(),
                            logicImpl.playerTieLogic.createSnapshot(),
                            Suit.Clubs
                          )
                        )
                      }

                    }
                  },new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AH.png")
                    fitWidth = 102
                    fitHeight = 150
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(this, Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        logicImpl.undoManager.doStep(
                          SelectTrumpSuitCommand(
                            logicImpl.createSnapshot(),
                            logicImpl.playerTieLogic.createSnapshot(),
                            Suit.Hearts
                          )
                        )
                      }


                    }
                  },
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AD.png")
                    fitWidth = 102
                    fitHeight = 150
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(this, Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        logicImpl.undoManager.doStep(
                          SelectTrumpSuitCommand(
                            logicImpl.createSnapshot(),
                            logicImpl.playerTieLogic.createSnapshot(),
                            Suit.Diamonds
                          )
                        )
                      }
                    }
                  },

                )
              },
              new Label {
                alignment = BottomCenter
                text = "Your Cards"
                font = Font.font(20)
              },
              new HBox {
                alignment = TopCenter
                alignment = BottomCenter
                spacing = 10
                margin = Insets(100, 0, 20, 0)
                children = player.currentHand().get.cards.map( i => new ImageView {
                  image = CardUtils.cardtoImage(i)
                  fitWidth = 170
                  fitHeight = 250
                })
              }
            )
      },
      )
    })
  }
}
