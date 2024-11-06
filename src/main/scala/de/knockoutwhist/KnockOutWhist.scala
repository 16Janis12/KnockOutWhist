package de.knockoutwhist

import de.knockoutwhist.control.MatchControl
import de.knockoutwhist.control.text.TextMatchControl


object KnockOutWhist {

  val matchControl: MatchControl = TextMatchControl
  /*
  Debug mode:

  - Disables the random shuffle of the cards
   */
  private[knockoutwhist] var DEBUG_MODE_VAR: Boolean = false

  def DEBUG_MODE = DEBUG_MODE_VAR

  def main(args: Array[String]): Unit = {
    if(!matchControl.initial()) throw new IllegalStateException("Game could not be started.")
  }
  
}