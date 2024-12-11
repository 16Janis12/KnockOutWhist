package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Node, Parent, Scene}

import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized

object GUIMain extends JFXApp3 with UI with EventListener {

  private var currentRoot: Parent = uninitialized

  override def initial: Boolean = {
    main(new Array[String](_length = 0))
    true
  }

  override def listen[R](event: ReturnableEvent[R]): Option[R] = ???

  override def start(): Unit = {
    currentRoot = MainMenu.createMainMenu
    val cont = ObjectProperty(currentRoot)
    JFXApp3.userAgentStylesheet_=(new PrimerDark().getUserAgentStylesheet)
    stage = new PrimaryStage {
      width = 800
      height = 600
      title = "Knockout Whist"
      scene = new Scene {
        root = currentRoot
        cont.onChange(Platform.runLater {
          root = currentRoot
        })
      }
    }
    stage.show()
  }


}