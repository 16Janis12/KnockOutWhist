package de.knockoutwhist

import de.knockoutwhist.cards.{CardManager, Player}
import de.knockoutwhist.control.text.TextPlayerControl


object KnockOutWhist {

  class KnockOutWhist {

  }
  def main(args: Array[String]): Unit = {
    /*CardManager.shuffleAndReset()
    val hand1 = CardManager.createHand()
    val handtoString = hand1.renderAsString()
    handtoString.foreach(println)*/
    val p1 = Player("Player 1")
    val p2 = Player("Player 2")
    val p3 = Player("Player 3")

    TextPlayerControl.determineWinnerTie(List(p1, p2, p3))
    


  }
}