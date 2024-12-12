package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.control.MainLogic
import de.knockoutwhist.utils.gui.Animations
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, TopCenter}
import scalafx.scene.control.{Button, Label, Slider, TextField}
import scalafx.geometry.Pos.{BottomCenter, TopCenter}
import scalafx.scene.Parent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

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
            MainLogic.startMatch()
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
      children = Seq(
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
          value.onChange((_, _, newValue) => {

          })
        }
      )
    })
  }

}
