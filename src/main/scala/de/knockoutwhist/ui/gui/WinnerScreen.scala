package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.player.AbstractPlayer
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, TopCenter}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

class WinnerScreen(gui: GUIMain) {
  private val winnerLabel: Label = new Label {
    text = ""
    alignment = Center
    font = Font.font(25)
  }
  private val actionButtons: HBox = new HBox {
    alignment = BottomCenter
    spacing = 10
    margin = Insets(0, 0, 150, 0)
    children = Seq(
      new Button {
        alignment = TopCenter
        text = "Yes"
        font = Font.font(20)
        styleClass += Styles.SUCCESS
        onMouseClicked = _ => {
          ControlThread.runLater {
            gui.logic.foreach(_.createSession())
          }
        }
      },
      new Button {
        alignment = TopCenter
        hgrow = Always
        text = "No"
        font = Font.font(20)
        styleClass += Styles.DANGER
        onMouseClicked = _ => 
          System.exit(0)
      }
    )
  }
  private val nextAction: Label = new Label {
    text = "Do you want to play another match?"
    alignment = BottomCenter
    font = Font.font(20)
    margin = Insets(50,0,0,0)
  }
  def spawnWinnerScreen(player: AbstractPlayer): Unit = {
    gui.mainMenu.changeChild(
      new VBox {
        alignment = TopCenter
        spacing = 10
        margin = Insets(50, 0, 150, 0)
        children = Seq(
          new Label {
            text = s"Congratulations! $player won this match of Knock-Out-Whist."
            alignment = TopCenter
            font = Font.font(35)
          },
          nextAction,
          actionButtons
        )
        //}
      },Duration(1000))

  }

}

