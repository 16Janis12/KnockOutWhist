package de.knockoutwhist.ui.gui

import de.knockoutwhist.cards.Suit
import de.knockoutwhist.control.{ControlHandler, ControlThread, PlayerLogic}
import de.knockoutwhist.events.directional.RequestPickTrumpsuitEvent
import de.knockoutwhist.events.ui.GameState.{INGAME, MAIN_MENU}
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.utils.gui.Animations
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, TopCenter}
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.util.Try

object PickTrumsuit {

  def showPickTrumpsuit(event: RequestPickTrumpsuitEvent): Unit = {
    MainMenu.changeChild(
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
              },
              new HBox {
                alignment = BottomCenter
                spacing = 10
                margin = Insets(200, 0, 20, 0)
                children = Seq(
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AS.png")
                    fitWidth = 170
                    fitHeight = 250
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        PlayerLogic.trumpSuitSelected(event.matchImpl, Try {
                          Suit.Spades
                        }, event.remaining_players, event.firstRound, event.player)
                      }

                    }
                  },
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AC.png")
                    fitWidth = 170
                    fitHeight = 250
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        PlayerLogic.trumpSuitSelected(event.matchImpl, Try {
                          Suit.Clubs
                        }, event.remaining_players, event.firstRound, event.player)
                      }

                    }
                  },new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AH.png")
                    fitWidth = 170
                    fitHeight = 250
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        PlayerLogic.trumpSuitSelected(event.matchImpl, Try {
                          Suit.Hearts
                        }, event.remaining_players, event.firstRound, event.player)
                      }


                    }
                  },
                  new ImageView {
                    alignment = BottomCenter
                    image = new Image("cards/AD.png")
                    fitWidth = 170
                    fitHeight = 250
                    onMouseClicked = _ => {
                      val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
                      slideOut.onFinished = _ => {
                        visible = false
                      }
                      slideOut.play()
                      ControlThread.runLater {
                        PlayerLogic.trumpSuitSelected(event.matchImpl, Try{Suit.Diamonds}, event.remaining_players, event.firstRound, event.player)
                      }
                    }
                  },

                )
              },
              new Label {
                alignment = TopCenter
                text = "Your Cards"
                font = Font.font(20)
              },
              new HBox {
                alignment = TopCenter
                alignment = BottomCenter
                spacing = 10
                margin = Insets(200, 0, 20, 0)
                children = event.player.currentHand().get.cards.map( i => new ImageView {
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
