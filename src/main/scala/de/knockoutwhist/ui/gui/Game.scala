package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.{MatchUtil, PlayerUtil}
import de.knockoutwhist.events.global.TrickEndEvent
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.Trick
import de.knockoutwhist.undo.commands.{PlayCardCommand, PlayDogCardCommand}
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.Implicits.*
import de.knockoutwhist.utils.gui.Animations
import javafx.scene.image
import javafx.scene.layout.{BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, CenterRight, TopCenter}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.Priority.{Always, Never}
import scalafx.scene.layout.{Background, BorderPane, HBox, VBox}
import scalafx.scene.text.{Font, TextAlignment}
import scalafx.scene.{Node, layout}
import scalafx.util.Duration

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class Game(gui: GUIMain) {
  
  private[ui] def reloadAll(): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Logic is not initialized!")
    val logic = gui.logic.get
    if (logic.getCurrentRound.isEmpty) throw new IllegalStateException("No current round available!")
    val currentRound = logic.getCurrentRound.get
    if (logic.getCurrentTrick.isEmpty) throw new IllegalStateException("No current trick available!")
    val currentTrick = logic.getCurrentTrick.get
    if (logic.getCurrentPlayer.isEmpty) throw new IllegalStateException("No current player available!")
    val currentPlayer = logic.getCurrentPlayer.get

    updateStatus(currentPlayer)
    updateNextPlayer(logic.getPlayerQueue.get, logic.getPlayerQueue.get.currentIndex)
    updatePlayedCards()
    if (currentTrick.firstCard.isDefined) {
      updateFirstCard(currentTrick.firstCard.get)
    } else {
      resetFirstCard()
    }
    updateTrumpSuit(currentRound.trumpSuit)
    updatePlayerCards(currentPlayer)
    createGame()
  }

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

  private val nextPlayers: VBox = new VBox {
    alignment = TopCenter
    spacing = 10
  }
  private val yourCardslabel: Label = new Label {
    alignment = Center
    text = "Your Cards"
    vgrow = Always
    font = Font.font(20)
    margin = Insets(50, 0, 0, 0)
  }
  private val playedCardslabel: Label = new Label {
    alignment = Center
    text = "Played Cards"
    vgrow = Always
    font = Font.font(20)
  }
  private val firstCardlabel: Label = new Label {
    alignment = TopCenter
    textAlignment = TextAlignment.Center
    text = "First Card: "
    vgrow = Always
    font = Font.font(24)
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
    gui.stage.maximized = true
    gui.mainMenu.changeChild(new BorderPane {
      val myBI = new BackgroundImage(new Image("/background.png", 32, 32, false, true),
        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
        BackgroundSize.DEFAULT)
      background = Background(Array(new layout.BackgroundImage(myBI)))
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
                text = "Save"
                font = Font.font(20)
                styleClass += Styles.WARNING
                onMouseClicked = _ => {
                  gui.logic.get.persistenceManager.saveFile("currentSnapshot")
                  new Alert(AlertType.Information) {
                    title = "Game Saved"
                    headerText = "Game Saved"
                    contentText = "This game was stored safely in currentSnapshot :)"
                  }.showAndWait()
                    
                }
              },
              new Button {
                hgrow = Never
                alignment = CenterRight
                text = "Undo"
                font = Font.font(20)
                styleClass += Styles.WARNING
                onMouseClicked = _ => {
                  gui.logic.get.undoManager.undoStep()
                }
              },
              new Button {
                hgrow = Never
                alignment = CenterRight
                text = "Exit Game"
                font = Font.font(20)
                styleClass += Styles.DANGER
                onMouseClicked = _ => {
                  gui.logic.get.endSession()
                }
              }
            )
          },
        )
      }
      center = new VBox {
        alignment = TopCenter
        children = Seq(
          playedCardslabel,
          playedCards,
        )
      }
      left = new VBox {
        margin = Insets(30, 0, 0, 30)
        alignment = TopCenter
        minWidth = 300
        maxWidth = 300
        children = Seq(
          new Label {
            alignment = TopCenter
            textAlignment = TextAlignment.Center
            text = "Next Players: "
            vgrow = Always
            font = Font.font(24)
          },
          nextPlayers
        )
      }
      right = new VBox {
        margin = Insets(30, 0, 0, 30)
        alignment = TopCenter
        minWidth = 300
        maxWidth = 300
        children = Seq(
          firstCardlabel,
          firstCard
        )
      }
      bottom = new VBox {
        alignment = BottomCenter
        children = Seq(
          yourCardslabel,
          playerCards,
        )
      }
    })
  }

  def updateStatus(player: AbstractPlayer): Unit = {
    val s = player.name.endsWith("s") ? "" |: "s"
    statusLabel.text = s"It's ${player.name}$s turn:"
    nextPlayers.visible = true
    playerCards.visible = true
    yourCardslabel.visible = true
    playedCardslabel.visible = true
    firstCardlabel.visible = true
    firstCard.visible = true
    suitLabel.visible = true
    nextPlayers.visible = true
  }

  def updateTrumpSuit(suit: Suit): Unit = {
    suitLabel.text = s"TrumpSuit: $suit"
  }

  def visibilityPlayerCards(visible: Boolean): Unit = {
    playerCards.visible = visible
  }

  private def firstCardvisible(visible: Boolean): Unit = {
    firstCard.visible = visible
  }

  def updateFirstCard(card: Card): Unit = {
    firstCardvisible(true)
    firstCard.image = CardUtils.cardtoImage(card)
  }

  def resetFirstCard(): Unit = {
    firstCard.image = new Image("cards/1B.png")
  }

  def updatePlayerCards(player: AbstractPlayer): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Logic is not initialized!")
    val logic = gui.logic.get
    if (logic.getCurrentMatch.isEmpty) throw new IllegalStateException("No current match available!")
    val currentMatch = logic.getCurrentMatch.get
    if (logic.getCurrentRound.isEmpty) throw new IllegalStateException("No current round available!")
    val currentRound = logic.getCurrentRound.get
    if (player.currentHand().isEmpty) throw new IllegalStateException("Player has no hand!")
    val hand: Hand = player.currentHand().get
    
    val cards = ListBuffer[Node]()
    playerCards.children.clear()
    for (card <- hand.cards) {
      cards += new ImageView {
        alignmentInParent = BottomCenter
        image = CardUtils.cardtoImage(card)
        fitWidth = 170
        fitHeight = 250
          onMouseClicked = _ => {
            if (logic.getCurrentTrick.isEmpty) throw new IllegalStateException("No current trick available!")
            val currentTrick = logic.getCurrentTrick.get
            if (logic.getCurrentPlayer.isDefined && logic.isWaitingForInput) {
              val currentPlayer = logic.getCurrentPlayer.get
              if (!currentPlayer.isInDogLife) {
                if (!PlayerUtil.canPlayCard(card, currentRound, currentTrick, currentPlayer)) {
                  val pulse = Animations.pulse(this, Duration(400))
                  pulse.play()
                } else {
                  hideCards(this)
                  val slideOut = Animations.slideOutUp(this, Duration(400), -350)
                  slideOut.onFinished = _ => {
                    visible = false
                    ControlThread.runLater {
                      logic.undoManager.doStep(
                        PlayCardCommand(
                          logic.createSnapshot(),
                          logic.playerTieLogic.createSnapshot(),
                          card
                        )
                      )
                    }
                  }
                  slideOut.play()
                }
              }
            }
          }
        }
      }
    if (player.isInDogLife && !MatchUtil.dogNeedsToPlay(currentMatch, currentRound)) {
      cards += new Button {
        alignmentInParent = BottomCenter
        styleClass += Styles.SUCCESS
        text = "Skip this turn"
        minWidth = 170
        maxWidth = 170
        minHeight = 250
        maxHeight = 250
        onMouseClicked = _ => {
          hideCards(this)
          val slideOutDog = Animations.slideOutUp(this, Duration(400), -350)
          slideOutDog.onFinished = _ => {
            visible = false
            ControlThread.runLater {
              logic.undoManager.doStep(
                PlayDogCardCommand(
                  logic.createSnapshot(),
                  logic.playerTieLogic.createSnapshot(),
                  None
                )
              )
            }
          }
          slideOutDog.play()
        }
      }
    }
    playerCards.children = cards.toList
  }
    
  private def visibilityPlayedCards(visible: Boolean): Unit = {
    playedCards.visible = visible
  }
  
  def updatePlayedCards(): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Logic is not initialized!")
    val logic = gui.logic.get
    if (logic.getCurrentTrick.isEmpty) throw new IllegalStateException("No current trick available!")
    val trick: Trick = logic.getCurrentTrick.get
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
          })
      }
    }
    playedCards.children = cards
  }

  private def hideCards(node: Node): Unit = {
    playerCards.children.foreach(child => {
      if(child != node.delegate) {
        child match
          case imageView: image.ImageView =>
            imageView.setImage(new Image("cards/1B.png"))
          case button: javafx.scene.control.Button =>
            button.setDisable(true)
          case _ =>
        val slideOut = Animations.slideOutDown(child, Duration(400), 350)
        slideOut.onFinished = _ => {
          child.setVisible(false)
        }
        slideOut.play()
      }
    })
  }

  def updateNextPlayer(queue: CustomPlayerQueue[AbstractPlayer], currendIndx: Int): Unit = {
    val queueDupli = queue.duplicate()
    nextPlayers.children = queueDupli.iteratorWithStart(currendIndx).map(player => new Label {
      text = !player.isInDogLife ? player.name |: s"${player.name} (Doglife)"
      font = Font.font(20)
    }).toSeq
  }

  def showWon(winner: AbstractPlayer, amountOfTricks: Int): Unit = {
    nextPlayers.visible = false
    playerCards.visible = false
    yourCardslabel.visible = false
    playedCardslabel.visible = false
    firstCardlabel.visible = false
    firstCard.visible = false
    suitLabel.visible = false
    nextPlayers.visible = false
    if (amountOfTricks == 1) statusLabel.text = s"${winner.name} won the round with $amountOfTricks trick!"
    else statusLabel.text = s"${winner.name} won the round with $amountOfTricks tricks!"
  }
  
  def showFinishedTrick(event: TrickEndEvent): Unit = {
    nextPlayers.visible = false
    playerCards.visible = false
    yourCardslabel.visible = false
    playedCardslabel.visible = false
    statusLabel.text = s"${event.winner.name} won the trick"
    updatePlayedCards()
  }
}