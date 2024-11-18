package de.knockoutwhist


import de.knockoutwhist.control.MatchControl
import de.knockoutwhist.ui.tui.TUIMain


object KnockOutWhist {
  
  /*
  Debug mode:

  - Disables the random shuffle of the cards
   */
  private[knockoutwhist] var DEBUG_MODE_VAR: Boolean = false

  def DEBUG_MODE = DEBUG_MODE_VAR

  def main(args: Array[String]): Unit = {
    if(!TUIMain.initial) throw new IllegalStateException("Game could not be started.")
  }
  
}