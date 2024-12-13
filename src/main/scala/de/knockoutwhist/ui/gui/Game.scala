package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.cards.{Card, Hand, Suit}
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
import scalafx.geometry.Pos.{BottomCenter, BottomLeft, Center, CenterLeft, CenterRight, TopCenter, TopLeft, TopRight}
import scalafx.scene.{Node, layout}
import scalafx.scene.control.{Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.{Always, Never}
import scalafx.scene.layout.{Background, BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.{Color, Paint}
import scalafx.scene.text.{Font, TextAlignment}
import scalafx.util.Duration

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized
import scala.util.Try


object Game {

  private val statusLabel: Label = new Label {
    alignment = Center
    textAlignment = TextAlignment.Center
    text = "It's {Player?}s turn:"
    font = Font.font(35)
    hgrow = Always
    maxWidth = Double.MaxValue
  }

  private val suitLabel: Label = new Label {
    alignment = Center
    textAlignment = TextAlignment.Left
    minWidth = 300
    maxWidth = 300
    text = "TrumpSuit: "
    font = Font.font(18)
    hgrow = Always
  }
  
  private val nextPlayers: HBox = new HBox {
    alignment = TopCenter
    minWidth = 300
    maxWidth = 300
    margin = Insets(30, 0, 0, 20)
    children = Seq(
      new Label {
        alignment = TopLeft
        text = "Next Players turn:"
        font = Font.font(20)
      }
    )
  }

  private val firstCard: ImageView = new ImageView {
    alignmentInParent = BottomCenter
    image = new Image("cards/1B.png")
    fitWidth = 170
    fitHeight = 250
    onMouseClicked = _ => System.exit(0)
  }

  private val playedCards: HBox = new HBox {
    alignment = BottomCenter
    spacing = 10
  }

  private val playerCards: HBox = new HBox {
    alignment = BottomCenter
    spacing = 10
    margin = Insets(30, 0, 20, 0)
  }

  def createGame(): Unit = {
    MainMenu.changeChild(new BorderPane {
      padding = Insets(10, 10, 10, 10)
      top = new HBox {
        alignment = Center
        hgrow = Always
        spacing = 0
        children = Seq(
          suitLabel,
          statusLabel,
          new HBox {
            hgrow = Always
            alignment = CenterRight
            spacing = 10
            minWidth = 300
            maxWidth = 300
            children = Seq(
              new Button {
                hgrow = Never
                alignment = CenterRight
                text = "Undo"
                font = Font.font(20)
                styleClass += Styles.WARNING
                onMouseClicked = _ => {
                  UndoManager.undoStep()
                }
              },
              new Button {
                hgrow = Never
                alignment = CenterRight
                text = "Exit Game"
                font = Font.font(20)
                styleClass += Styles.DANGER
                onMouseClicked = _ => System.exit(0)
              }
            )
          },
        )
      }
      center = new VBox {
        alignment = TopCenter
        children = Seq(
          new Label {
            alignment = Center
            text = "Played Cards"
            vgrow = Always
            font = Font.font(20)
          },
          playedCards,
        )
      }
      left = nextPlayers
      right = new VBox {
        alignment = Center
        minWidth = 300
        maxWidth = 300
        children = Seq(
          new Label {
            alignment = Center
            textAlignment = TextAlignment.Center
            text = "First Card: "
            vgrow = Always
            font = Font.font(24)
          },
          firstCard
        )
      }
      bottom = new VBox {
        alignment = BottomCenter
        children = Seq(
          new Label {
            alignment = Center
            text = "Your Cards"
            vgrow = Always
            font = Font.font(20)
            margin = Insets(50,0,0,0)
          },
          playerCards,
        )
      }
    })
  }
  
  def updateStatus(player: AbstractPlayer): Unit = {
    statusLabel.text = s"It's ${player.name}s turn:"
  }

  def updateTrumpSuit(suit: Suit): Unit = {
    suitLabel.text = s"TrumpSuit: $suit"
  }

  def visibilityPlayerCards(visible: Boolean): Unit = {
    playerCards.visible = visible
  }

  def firstCardvisible(visible: Boolean): Unit = {
    firstCard.visible = visible
  }

  def updateFirstCard(card: Card): Unit = {
    firstCardvisible(true)
    firstCard.image = CardUtils.cardtoImage(card)
  }

  def resetFirstCard(): Unit = {
    firstCard.image = new Image("cards/1B.png")
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
            if (TrickLogic.alternativeCards(card, event.round, event.trick, event.player).nonEmpty) {
              val pulse = Animations.pulse(this, Duration(400))
              pulse.play()
            } else {
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