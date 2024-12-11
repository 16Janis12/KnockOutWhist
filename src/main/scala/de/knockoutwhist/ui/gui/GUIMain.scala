package de.knockoutwhist.ui.gui

import atlantafx.base.theme.PrimerDark
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.events.{EventListener, ReturnableEvent}
import scalafx.application.JFXApp3

object GUIMain extends JFXApp3 with UI with EventListener {

  override def initial: Boolean = {
    main(new Array[String](_length = 0))
    true
  }

  override def listen[R](event: ReturnableEvent[R]): Option[R] = ???

  override def start(): Unit = {
    JFXApp3.userAgentStylesheet_=(new PrimerDark().getUserAgentStylesheet)

  }


}
