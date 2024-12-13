package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.control.{ControlHandler, ControlThread, TrickLogic}
import de.knockoutwhist.events.directional.RequestCardEvent
import de.knockoutwhist.player.Playertype.HUMAN
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory}
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.undo.UndoManager
import de.knockoutwhist.undo.commands.EnterPlayersCommand
import de.knockoutwhist.utils.gui.Animations
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, BottomLeft, Center, TopCenter, TopLeft}
import scalafx.scene.Node
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized
import scala.util.Try


object Game {

  private val statusLabel: Label = new Label {
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
            statusLabel,
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
  
  def updateStatus(player: AbstractPlayer): Unit = {
    statusLabel.text = s"It's ${player.name}s turn:"
  }

  def visibilityPlayerCards(visible: Boolean): Unit = {
    playerCards.visible = visible
  }

  def updatePlayerCards(hand: Hand): Unit = {
    val cards = ListBuffer[Node]()
    for (card <- hand.cards) {
      cards += new ImageView {
        alignmentInParent = BottomCenter
        image = CardUtils.cardtoImage(card)
        fitWidth = 170
        fitHeight = 250
        onMouseClicked = _ => {
          if(requestCard.isDefined) {
            val event = requestCard.get
            val slideOut = Animations.slideOutUp(this, Duration(400), -350)
            slideOut.onFinished = _ => {
              visible = false
              ControlThread.runLater {
                TrickLogic.controlSuitplayed(Try {
                  card
                }, event.matchImpl, event.round, event.trick, event.currentIndex, event.player)
              }
              requestCard = None
            }
            slideOut.play()
          }
        }
      }
    }
    playerCards.children = cards
  }

  def visibilityPlayedCards(visible: Boolean): Unit = {
    playedCards.visible = visible
  }

  def updatePlayedCards(trick: Trick): Unit = {
    visibilityPlayedCards(true)
    val cards = ListBuffer[Node]()
    for (card <- trick.cards) {

      cards += new VBox {
        children = Seq(
          new Label {
            text = card._2.toString
            font = Font.font(10)
            margin = Insets(0, 0, 0, 0)
          },
          new ImageView {
          alignmentInParent = BottomCenter
          image = CardUtils.cardtoImage(card._1)
          fitWidth = 102
          fitHeight = 150
          onMouseClicked = _ => System.exit(0)
        })
      }
    }
    playedCards.children = cards
  }

  private[gui] var requestCard: Option[RequestCardEvent] = None

  def showWon(round: Round): Unit = {
    val playerwon = round.winner
    statusLabel.text = s"${playerwon.name} won the round!"
  }
}