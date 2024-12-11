package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, TopCenter}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.text.Font

object MainMenu {
  def createMainMenu: StackPane = {
    new StackPane {
      children = Seq(
        new VBox {
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
        }
      )
    }
  }

}
