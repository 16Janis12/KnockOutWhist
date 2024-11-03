package de.knockoutwhist

import de.knockoutwhist.control.MatchControl
import de.knockoutwhist.control.text.TextMatchControl


object KnockOutWhist {

  val matchControl: MatchControl = TextMatchControl
  /*
  Debug mode:

  - Disables the random shuffle of the cards
   */
  val DEBUG_MODE: Boolean = true

  def main(args: Array[String]): Unit = {
    if(!matchControl.initial()) throw new IllegalStateException("Game could not be started.")
    /*CardManager.shuffleAndReset()
    val hand1 = CardManager.createHand()
    val handtoString = hand1.renderAsString()
    handtoString.foreach(println)*/
    //val p1 = Player("Player 1")
    //val p2 = Player("Player 2")
    //val p3 = Player("Player 3")
    //TextPlayerControl.determineWinnerTie(List(p1, p2, p3))
  }
  
}