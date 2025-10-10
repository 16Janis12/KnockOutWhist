package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.{ControlHandler, ControlThread}
import de.knockoutwhist.events.ui.GameState.MAIN_MENU
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.persistence.stub.PersistenceBaseManager
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.ui.tui.TUIMain
import de.knockoutwhist.utils.gui.Animations
import javafx.scene.{Node, control}
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, TopCenter, TopLeft, TopRight}
import scalafx.scene.Parent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label, Slider, TextField}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.text.{Font, TextAlignment}
import scalafx.util.Duration

import java.awt.Taskbar.Feature
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object MainMenu {

  private val mainMenu: StackPane = new StackPane()
  
  def current_root: Parent = mainMenu

  def createMainMenu: StackPane = {
    GUIMain.stage.maximized = false
    GUIMain.stage.resizable = true
    changeChild(new VBox {
      alignment = Center
      spacing = 10
      margin = Insets(0, 0, 150, 0)
      children = Seq(
        new ImageView {
          image = new Image("/KnockOutLogo.png")
          fitWidth = 200
          fitHeight = 200
        },
        new Button {
          alignment = TopCenter
          hgrow = Always
          text = "Start Game"
          font = Font.font(25)
          styleClass += Styles.SUCCESS
          onMouseClicked = _ => {
            ControlThread.runLater {
              KnockOutWhist.config.maincomponent.startMatch()
            }
          }
        },
        new Button {
          alignment = TopCenter
          hgrow = Always
          text = "Exit Game"
          font = Font.font(20)
          styleClass += Styles.DANGER
          onMouseClicked = _ => System.exit(0)
        },
        new Button {
          alignment = TopCenter
          hgrow = Always
          text = "Load Game"
          font = Font.font(20)
          styleClass += Styles.ACCENT
          disable = !KnockOutWhist.config.persistenceManager.canLoadfile("currentSnapshot")
          onMouseClicked = _ => KnockOutWhist.config.persistenceManager.loadFile("currentSnapshot")
        }
      )
    }, Duration(1000))
    mainMenu
  }

  def changeChild(child: Parent, duration: Duration = Duration(500)): Unit = {
    val times = ListBuffer[Timeline]()
    mainMenu.children.foreach(node => {
      times += Animations.fadeOutLeft(node, duration)
      node.setDisable(true)
    })
    val fadeIn = Animations.fadeInRight(child, duration)
    mainMenu.children += child
    times.foreach(_.play())
    fadeIn.play()
    fadeIn.onFinished = _ => {
      mainMenu.children = Seq(child)
    }
  }
  def createPlayeramountmenu(): Unit = {
    GUIMain.stage.maximized = true
    changeChild(new BorderPane {
      margin = Insets(50, 50, 50, 50)
      val players: VBox = new VBox {
        vgrow = Always
        alignment = TopCenter
        spacing = 20
        margin = Insets(0, 0, 50, 0)
      }
      top = new HBox() {
        alignment = TopCenter
        children = new ImageView {
          image = new Image("/KnockOutLogo.png")
          fitWidth = 200
          fitHeight = 200
        }
      }
      left =  new HBox {
        alignment = TopCenter
        children = new Button {
          alignment = TopRight
          styleClass += Styles.BUTTON_CIRCLE
          styleClass += Styles.ACCENT
          graphic = new ImageView {
            image = new Image("return-icon.png")
            fitWidth = 20
            fitHeight = 20
          }
          onMouseClicked = _ => {
            ControlThread.runLater {
              ControlHandler.invoke(GameStateUpdateEvent(MAIN_MENU))
            }
          }
        }
      }
      right = new HBox {
        alignment = TopCenter
        children = new Button {
          styleClass += Styles.SUCCESS
          styleClass += Styles.BUTTON_CIRCLE
          graphic = new ImageView {
            image = new Image("checkmark.png")
            fitWidth = 20
            fitHeight = 20
          }
          onMouseClicked = _ => {
            val usedNames = ListBuffer[String]()
            val playerNamesList = ListBuffer[AbstractPlayer]()
            players.children.foreach {
              case field: control.TextField =>
                if (field.getText.nonEmpty && !usedNames.contains(field.getText)) {
                  usedNames += field.getText
                  playerNamesList += PlayerFactory.createPlayer(field.getText, playertype = HUMAN)
                }
              case _ =>
            }
            if(playerNamesList.size < 2) {
              new Alert(AlertType.Error) {
                title = "Enter Names"
                headerText = "Enter Names " + playerNamesList.size
                contentText = "You need to enter at least 2 different names in order to play!"
              }.showAndWait()
            }else {
              ControlThread.runLater {
                KnockOutWhist.config.maincomponent.enteredPlayers(playerNamesList.toList)
              }
            }
          }
        }
      }
      center = new VBox{
        alignment = TopCenter
        children = Seq(
          new Label {
            alignment = TopCenter
            textAlignment = TextAlignment.Center
            text = "Select Playeramount below:"
            font = Font.font(30)
          },
          new Slider {
            min = 2
            max = 7
            showTickLabels = true
            showTickMarks = true
            majorTickUnit = 1
            minorTickCount = 0
            snapToTicks = true
            maxWidth = 450
            maxHeight = 30
            value.onChange((_, oldvalue, newvalue) => {
              if(oldvalue.intValue() > newvalue.intValue()) {
                for (i <- oldvalue.intValue()-1 to(newvalue.intValue(), -1)) {
                  players.children.remove(i)
                }
              }else if(oldvalue.intValue() < newvalue.intValue()) {
                for (i <- oldvalue.intValue() + 1 to newvalue.intValue()) {
                  players.children.add(new TextField {
                    promptText = s"Enter Player $i"
                    visible = true
                    maxWidth = 450
                    maxHeight = 30
                  })
                }
              }
            })
          },
          players
        )
      }
      for (i <- 1 to 2) {
        players.children.add(new TextField {
          promptText = s"Enter Player $i"
          visible = true
          maxWidth = 450
          maxHeight = 30
        })
      }
    })
  }

}
