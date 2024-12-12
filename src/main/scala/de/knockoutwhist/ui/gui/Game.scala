package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.EnterPlayersCommand
import de.knockoutwhist.utils.gui.Animations
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, BottomLeft, Center, TopCenter, TopLeft}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized


object Game {

  private val playersTurnLabel: Label = new Label {
    alignment = TopCenter
    text = "It's {Player?}s turn:"
    font = Font.font(35)
    hgrow = Always
  }
  
  private val nextPlayers: HBox = new HBox {
    alignment = TopLeft
    children = Seq(
      new Label {
        alignment = TopLeft
        text = "Next Players turn:"
        font = Font.font(20)
        margin = Insets(30, 0, 0, 20)
      }
    )
  }

  private val playedCards: HBox = new HBox {
    alignment = BottomCenter
    spacing = 10
    margin = Insets(0, 0, 20, 0)
    children = Seq(
      new ImageView {
        alignment = BottomCenter
        image = new Image("cards/TH.png")
        fitWidth = 102
        fitHeight = 150
        onMouseClicked = _ => System.exit(0)
      },
    )
  }

  private val playerCards: HBox = new HBox {
    alignment = BottomCenter
    spacing = 10
    margin = Insets(30, 0, 20, 0)
    children = Seq(
      new ImageView {
        alignment = BottomCenter
        image = new Image("cards/3H.png")
        fitWidth = 170
        fitHeight = 250
        onMouseClicked = _ => {
          val slideOut = Animations.slideOutUp(children.head.asInstanceOf[javafx.scene.image.ImageView], Duration(400), -350)
          slideOut.onFinished = _ => {
            visible = false
          }
          slideOut.play()
        }
      },
      new ImageView {
        alignment = BottomCenter
        image = new Image("cards/4C.png")
        fitWidth = 170
        fitHeight = 250
        onMouseClicked = _ => System.exit(0)
      },
    )
  }

  def createGame(): Unit = {
    MainMenu.changeChild(new StackPane {
      children = Seq(
        new VBox {
          alignment = TopCenter
          spacing = 10
          margin = Insets(20, 0, 0, 0)
          hgrow = Always

          children = Seq(
            playersTurnLabel,
            nextPlayers,
            new Label {
              alignment = Center
              text = "Played Cards"
              vgrow = Always
              font = Font.font(20)
              margin = Insets(50,0,0,0)
            },
            playedCards,
            new Label {
              alignment = Center
              text = "Your Cards:"
              font = Font.font(20)
              margin = Insets(35,0,0,0)
            },
            playerCards,
          )
        }
      )
    })
  }
  
  def updateTurn(player: AbstractPlayer): Unit = {
    playersTurnLabel.text = s"It's ${player.name}s turn:"
  }
  
  def matchcard(card: Card): Boolean = {
    true
  }
}