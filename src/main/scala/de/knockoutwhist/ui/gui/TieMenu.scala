package de.knockoutwhist.ui.gui

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.gui.Animations
import scalafx.animation.Timeline
import scalafx.geometry.Insets
import scalafx.geometry.Pos.{BottomCenter, Center}
import scalafx.scene.Parent
import scalafx.scene.control.Label
import scalafx.scene.layout.Priority.Always
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.util.Duration

import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized

object TieMenu {

  private val tieMenu: StackPane = new StackPane()
  def current_root: Parent = tieMenu

  def spawnTieMain(): StackPane = {
    changeChild(new VBox {
      alignment = Center
      spacing = 10
      margin = Insets(0, 0, 150, 0)
      children = Seq(
        new Label {
          text = "The last round was a tie!"
        }

      )
    }, Duration(1000))
    tieMenu
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
