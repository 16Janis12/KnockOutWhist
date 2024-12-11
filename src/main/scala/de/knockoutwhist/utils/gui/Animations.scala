package de.knockoutwhist.utils.gui

import javafx.animation.Interpolator
import javafx.scene.Node
import scalafx.animation.{KeyFrame, KeyValue, Timeline}
import scalafx.util.Duration

object Animations {

  def fadeInRight(node: Node, duration: Duration, startX: Int = 500): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.opacityProperty, 0, Interpolator.EASE_IN),
          KeyValue(node.translateXProperty, startX, Interpolator.EASE_IN)
        )
        ),
        KeyFrame(duration, values = Set(
          KeyValue(node.opacityProperty, 1, Interpolator.EASE_IN),
          KeyValue(node.translateXProperty, 0, Interpolator.EASE_IN)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setOpacity(1)
      node.setTranslateX(0)
    }
    timeline
  }

  def fadeOutLeft(node: Node, duration: Duration, endX: Int = -500): Timeline = {
    val timeline = new Timeline {
      keyFrames = Seq(
        KeyFrame(Duration.Zero, values = Set(
          KeyValue(node.opacityProperty(), 1, Interpolator.EASE_OUT),
          KeyValue(node.translateXProperty(), 0, Interpolator.EASE_OUT)
        )
        ),
        KeyFrame(duration, values = Set(
          KeyValue(node.opacityProperty(), 0, Interpolator.EASE_OUT),
          KeyValue(node.translateXProperty(), endX, Interpolator.EASE_OUT)
        ))
      )
    }
    timeline.onFinished = _ => {
      node.setOpacity(1)
      node.setTranslateX(endX)
    }
    timeline
  }


}
