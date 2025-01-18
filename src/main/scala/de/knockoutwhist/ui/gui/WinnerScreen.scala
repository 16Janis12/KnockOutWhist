package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.ui.gui.TieMenu.{nextPlayer, selectButton, selectedCutCards, slider, tiewinner, toplabel}
import scalafx.geometry.Insets
import scalafx.geometry.Pos.TopCenter
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

object WinnerScreen {
  private val winnerLabel: Label = new Label {
    text = ""
    alignment = TopCenter
    font = Font.font(25)
  }
  private val actionButtons: HBox = new HBox {
    children = Seq(
      new Button {
        text = "Yes"
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
        text = "No"
        font = Font.font(20)
        styleClass += Styles.DANGER
        onMouseClicked = _ => 
          System.exit(0)
      }
    )
  }
  private val nextAction: Label = new Label {
    text = "Do you want to player another match?"
    alignment = TopCenter
    font = Font.font(20)
  }
  def spawnWinnerScreen(player: AbstractPlayer): Unit = {
    MainMenu.changeChild(
      new VBox {
        alignment = TopCenter
        spacing = 10
        margin = Insets(50, 0, 150, 0)
        children = Seq(
          new Label {
            text = s"Congratulations! $player won this match of Knock-Out-Whist."
            alignment = TopCenter
            font = Font.font(25)
          },
          nextAction,
          actionButtons
        )
        //}
      },Duration(1000))

  }

}

