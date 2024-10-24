package de.knockoutwhist

import de.knockoutwhist.cards.CardManager


object KnockOutWhist {

  class KnockOutWhist {

  }
  def main(args: Array[String]): Unit = {
    val hand1 = CardManager.createHand()
    val handtoString = hand1.renderAsString()
    handtoString.foreach(println)

    


  }
}