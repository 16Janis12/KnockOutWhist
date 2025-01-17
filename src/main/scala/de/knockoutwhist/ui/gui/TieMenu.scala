package de.knockoutwhist.ui.gui

import atlantafx.base.theme.Styles
import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.Card
import de.knockoutwhist.control.{ControlHandler, ControlThread}
import de.knockoutwhist.events.ShowPlayerStatus
import de.knockoutwhist.events.directional.{RequestCardEvent, RequestTieNumberEvent}
import de.knockoutwhist.events.ui.GameState.MAIN_MENU
import de.knockoutwhist.events.ui.GameStateUpdateEvent
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round}
import de.knockoutwhist.utils.gui.Animations
import javafx.scene.layout.{BackgroundImage, BackgroundPosition, BackgroundRepeat, BackgroundSize}
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center, TopCenter}
import scalafx.scene.{Parent, layout}
import scalafx.scene.control.{Button, Label, Slider}
import scalafx.scene.image.Image
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{Background, BorderPane, StackPane, VBox}
import scalafx.scene.text.Font
import scalafx.util.Duration

import scala.collection.immutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized
import scala.util.Try

object TieMenu {

  private val tieMenu: StackPane = new StackPane()
  def current_root: Parent = tieMenu

  private val nextPlayer: Label = new Label {
    visible = false
  }
  private val slider: Slider = new Slider {
    alignmentInParent = BottomCenter
    min = 1
    max = 51
    showTickLabels = true
    showTickMarks = true
    majorTickUnit = 1
    minorTickCount = 0
    snapToTicks = true
    maxWidth = 1000
    maxHeight = 60
  }
  def changeSlider(maxNumber: Int): Unit = {
    slider.max = maxNumber
  }
  def updatePlayerLabel(player: AbstractPlayer): Unit = {
    nextPlayer.visible = true
    nextPlayer.font = Font.font(20)
    nextPlayer.text = s"It's $player's turn to select the tie card."
  }
  private[gui] var requestInfo: Option[RequestTieNumberEvent] = None
  def spawnTieMain(): Unit = {
    MainMenu.changeChild(//new StackPane {
//      val myBI = new BackgroundImage(new Image("/background.png", 32, 32, false, true),
//        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, 
//        BackgroundSize.DEFAULT)
//      background = Background(Array(new layout.BackgroundImage(myBI)))
//      padding = Insets(10, 10, 10, 10)
      new VBox {
        alignment = TopCenter
        spacing = 10
        margin = Insets(50, 0, 150, 0)
        children = Seq(
          new Label {
            alignment = TopCenter
            text = "The last round was a tie! Let's cut to determine the winner"
            font = Font.font(50)
            spacing = 10
          },
          nextPlayer,
          slider,
          new Button {
            text = "Select"
            styleClass += Styles.ACCENT
            onMouseClicked = _ => {
              if (requestInfo.isDefined) {
                val event = requestInfo.get
                ControlThread.runLater {
                  KnockOutWhist.config.playerlogcomponent.selectedTie(event.winner, event.matchImpl, event.round, event.playersout, event.cut, Try(slider.value.toInt), event.currentStep, event.remaining, event.currentIndex)
                }
              }
              slider.value = 1
            }
          })
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
