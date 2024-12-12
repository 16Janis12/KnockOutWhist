package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.events.cards.RenderHandEvent
import de.knockoutwhist.events.directional.RequestPlayersEvent
import de.knockoutwhist.events.guiTransmitter.AnswerPlayerList
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent, SimpleEvent}
import scalafx.application.{JFXApp3, Platform}
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.scene.{Node, Parent, Scene}

import java.util
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.compiletime.uninitialized
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object GUIMain extends Thread with JFXApp3 with UI with EventListener {
  private var currentRoot: Parent = uninitialized
  
  override def run(): Unit  = {
    initial
  }

  override def initial: Boolean = {
    main(new Array[String](_length = 0))
    true
  }

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

  override def listen[R](event: ReturnableEvent[R]): Option[R] = {
      event match {
        case event: RequestPlayersEvent =>
          EventHandler.handleRequestEvent(event)
      }
    }
    


  }

  object EventHandler extends EventListener {

    private val resultHashMap: mutable.HashMap[Class[?], SimpleEvent] = mutable.HashMap[Class[?], SimpleEvent]()

    override def listen[R](event: ReturnableEvent[R]): Option[R] = {
      event match {
        case event: AnswerPlayerList =>
          resultHashMap.put(event.getClass, event)
          Some(true)
        case _ => None
      }
    }

    def handleRequestEvent(requestPlayersEvent: RequestPlayersEvent): Option[List[AbstractPlayer]] = {
      if (resultHashMap.contains(classOf[AnswerPlayerList])) {
        val answerPlayerList = resultHashMap(classOf[AnswerPlayerList]).asInstanceOf[AnswerPlayerList]
        Some(answerPlayerList.playerList)
      } else {
        None
      }

    }
  }
}
