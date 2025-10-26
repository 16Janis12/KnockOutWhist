package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.{ControlThread, GameLogic}
import de.knockoutwhist.events.global.tie.{TieTieEvent, TieWinningPlayersEvent}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.undo.commands.SelectTieNumberCommand
import de.knockoutwhist.utils.gui.Animations
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, TopCenter}
import scalafx.scene.control.{Button, Label, Slider}
import scalafx.scene.image.ImageView
import scalafx.scene.layout.*
import scalafx.scene.text.Font
import scalafx.scene.{Node, Parent, layout}
import scalafx.util.Duration

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized

class TieMenu(gui: GUIMain) {

  private[ui] def reloadAll(): Unit = {
    if (gui.logic.isEmpty) throw new IllegalStateException("Logic is not initialized!")
    val logic = gui.logic.get

    val player = logic.playerTieLogic.currentTiePlayer()
    updatePlayerLabel(player)

    changeSlider(logic.playerTieLogic.highestAllowedNumber())
    showNeccessary()
    spawnTieMain()
  }
  
  private def logic: GameLogic = {
    gui.logic.get
  }

  private val tieMenu: StackPane = new StackPane()
  def current_root: Parent = tieMenu

  private val nextPlayer: Label = new Label {
    visible = false
  }
  private val slider: Slider = new Slider {
    alignmentInParent = BottomCenter
    min = 1
    max = 51
    visible = true
    showTickLabels = true
    showTickMarks = true
    majorTickUnit = 1
    minorTickCount = 0
    snapToTicks = true
    maxWidth = 1000
    maxHeight = 60
    value.onChange((_, oldvalue, newvalue) => {
      value = newvalue.doubleValue()
  })
  }
  private val toplabel: Label = new Label {
    alignment = TopCenter
    visible = true
    text = "The last round was a tie! Let's cut to determine the winner"
    font = Font.font(50)
  }
  private val selectedCutCards: HBox = new HBox {
      alignment = BottomCenter
      spacing = 10
      margin = Insets(50, 0, 150, 0)
      visible = false
      children = Seq()
  }
  private val tiewinner: Label = new Label {
    alignment = TopCenter
    font = Font.font(30)
    visible = true
  }
  private val selectButton: Button = new Button {
    text = "Select"
    styleClass += Styles.ACCENT
    onMouseClicked = _ => {
      if (slider.value.toInt >= 1 && slider.value.toInt <= logic.playerTieLogic.highestAllowedNumber() && logic.isWaitingForInput) {
        ControlThread.runLater {
          logic.undoManager.doStep(
            SelectTieNumberCommand(
              logic.createSnapshot(),
              logic.playerTieLogic.createSnapshot(),
              slider.value.toInt
            )
          )
        }
      }
    }
  }
  def updateWinnerLabel(event: TieWinningPlayersEvent) : Unit = {
    if (event.isSingleWinner) {
      tiewinner.text = s"${event.winners.head.name} wins the cut!"
    } else {
      tiewinner.text = ""
    }
    slider.visible = false
    toplabel.visible = false
    nextPlayer.visible = false
    selectButton.visible = false
  }
  def showNeccessary(): Unit = {
    tiewinner.text = ""
    slider.visible = true
    toplabel.visible = true
    nextPlayer.visible = true
    selectButton.visible = true
    selectedCutCards.visible = false
  }
  def showTieAgain(event: TieTieEvent): Unit = {
    tiewinner.text = "It's a tie again! Let's cut again."
    slider.visible = false
    selectButton.visible = false
    toplabel.visible = false
    nextPlayer.visible = false
  }
  def addCutCards(list: List[(AbstractPlayer, Card)]): Unit = {
    selectedCutCards.visible = true
    val cards = ListBuffer[Node]()
    for (e <- list) {
      cards += new VBox {
        alignment = BottomCenter
        children = Seq(
          new Label {
            text = s"${e._1}"
          },
          new ImageView {
            alignmentInParent = BottomCenter
            image = CardUtils.cardtoImage(e._2)
            fitWidth = 170
            fitHeight = 250
        })
      }
      selectedCutCards.children = cards
    }
    
  }
  def hideCutCards(): Unit = {
    selectedCutCards.visible = false
  }
  def changeSlider(maxNumber: Int): Unit = {
    slider.max = maxNumber
  }
  def updatePlayerLabel(player: AbstractPlayer): Unit = {
    nextPlayer.visible = true
    nextPlayer.font = Font.font(20)
    nextPlayer.text = s"It's $player's turn to select the tie card."
  }
  def spawnTieMain(): Unit = {
    gui.mainMenu.changeChild(
      new VBox {
        alignment = TopCenter
        spacing = 10
        margin = Insets(50, 0, 150, 0)
        children = Seq(
          toplabel,
          nextPlayer,
          slider,
          selectButton,
          tiewinner,
          selectedCutCards
        )
      //}
    },Duration(1000))
  }

  def changeChild(child: Parent, duration: Duration = Duration(500)): Unit = {
    val times = ListBuffer[Timeline]()
    tieMenu.children.foreach(node => times += Animations.fadeOutLeft(node, duration))
    val fadeIn = Animations.fadeInRight(child, duration)
    tieMenu.children += child
    times.foreach(_.play())
    fadeIn.play()
    fadeIn.onFinished = _ => {
      tieMenu.children = Seq(child)
    }
  }

}

object TieData {
  var winners: List[AbstractPlayer] = uninitialized
  var remainingCards: Int = uninitialized
}
