package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.utils.gui.Animations
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, TopCenter, TopLeft}
import scalafx.scene.control.{Button, Label, Slider, TextField}
import scalafx.scene.Parent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import java.awt.Taskbar.Feature
import scala.collection.mutable.ListBuffer

object MainMenu {

  private val mainMenu: StackPane = new StackPane()


  def createMainMenu: StackPane = {
    changeChild(new VBox {
      alignment = BottomCenter
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
            createPlayeramountmenu()
          }
        },
        new Button {
          alignment = TopCenter
          hgrow = Always
          text = "Exit Game"
          font = Font.font(20)
          styleClass += Styles.DANGER
          onMouseClicked = _ => System.exit(0)
        }
      )
    }, Duration(1000))
    mainMenu
  }

  private def changeChild(child: Parent, duration: Duration = Duration(500)): Unit = {
    val times = ListBuffer[Timeline]()
    mainMenu.children.foreach(node => times += Animations.fadeOutLeft(node, duration))
    val fadeIn = Animations.fadeInRight(child, duration)
    mainMenu.children += child
    times.foreach(_.play())
    fadeIn.play()
    fadeIn.onFinished = _ => {
      mainMenu.children = Seq(child)
    }
  }
  def createPlayeramountmenu(): Unit = {
    changeChild(new VBox {
      alignment = TopCenter
      spacing = 20
      margin = Insets(100, 0, 0, 0)
      val players: VBox = new VBox {
        alignment = BottomCenter
        spacing = 20
        margin = Insets(0, 0, 50, 0)
      }
      children = Seq(
        new VBox {
          alignment = TopCenter
          children = Seq(
            new HBox {
              alignment = TopCenter
              spacing = 500
              children = Seq(
                new Button {
                  styleClass += Styles.BUTTON_CIRCLE
                  styleClass += Styles.ACCENT
                  graphic = new ImageView {
                    image = new Image("return-icon.png")
                    fitWidth = 20
                    fitHeight = 20
                }
                  onMouseClicked = _ => {
                    createMainMenu
                  }
            },
                new Button {
                  styleClass += Styles.SUCCESS
                  styleClass += Styles.BUTTON_CIRCLE
                  graphic = new ImageView {
                    image = new Image("checkmark.png")
                    fitWidth = 20
                    fitHeight = 20
                  }
                  onMouseClicked = _ => {
                    //startgame()
                  }
                  
                }
              )
            }
          )
        },
        new Label {
          alignment = TopCenter
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
            players.children.clear()
              for (i <- 1 to newvalue.intValue()) {
                players.children.add(new TextField {
                  promptText = "Enter Player"
                  visible = true
                  maxWidth = 450
                  maxHeight = 30
                })
              }
          })

        },
        players

//        new VBox {
//          alignment = BottomCenter
//          spacing = 20
//          margin = Insets(0, 0, 50, 0)
//          for (i <- 1 to 5) {
//              children.add(new TextField {
//                promptText = "Enter Player 1"
//                visible = true
//              })
//          }
//        }
      )
    })
  }

}
