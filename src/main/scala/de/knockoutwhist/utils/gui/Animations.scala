package de.knockoutwhist.utils.gui

import javafx.animation.Interpolator
import javafx.scene.Node
import scalafx.animation.{KeyFrame, KeyValue, Timeline}
import scalafx.util.Duration

object Animations {

  def fadeInRight(node: Node, duration: Duration, startX: Int = 200): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.opacityProperty, 0, Interpolator.EASE_BOTH),
          KeyValue(node.translateXProperty, startX, Interpolator.EASE_BOTH)
        )
        ),
        KeyFrame(duration, values = Set(
          KeyValue(node.opacityProperty, 1, Interpolator.EASE_BOTH),
          KeyValue(node.translateXProperty, 0, Interpolator.EASE_BOTH)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setOpacity(1)
      node.setTranslateX(0)
    }
    timeline
  }

  def fadeOutLeft(node: Node, duration: Duration, endX: Int = -200): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.opacityProperty(), 1, Interpolator.EASE_BOTH),
          KeyValue(node.translateXProperty(), 0, Interpolator.EASE_BOTH)
        )
        ),
        KeyFrame(duration, values = Set(
          KeyValue(node.opacityProperty(), 0, Interpolator.EASE_BOTH),
          KeyValue(node.translateXProperty(), endX, Interpolator.EASE_BOTH)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setOpacity(1)
      node.setTranslateX(endX)
    }
    timeline
  }

  def slideOutUp(node: Node, duration: Duration, endY: Int = -500): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.translateYProperty(), 0, Interpolator.EASE_BOTH)
        )
        ),
        KeyFrame(duration, values = Set(
          KeyValue(node.translateYProperty(), endY, Interpolator.EASE_BOTH)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setTranslateY(endY)
    }
    timeline
  }

  def pulse(node: Node, duration: Duration, pulseScale: Double = 1.1): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH),
          KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH)
        )
        ),
        KeyFrame(new Duration(duration.divide(2)), values = Set(
          KeyValue(node.scaleXProperty(), pulseScale, Interpolator.EASE_BOTH),
          KeyValue(node.scaleYProperty(), pulseScale, Interpolator.EASE_BOTH)
        )),
        KeyFrame(duration, values = Set(
          KeyValue(node.scaleXProperty(), 1, Interpolator.EASE_BOTH),
          KeyValue(node.scaleYProperty(), 1, Interpolator.EASE_BOTH)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setScaleX(1)
      node.setScaleY(1)
    }
    timeline
  }




}
