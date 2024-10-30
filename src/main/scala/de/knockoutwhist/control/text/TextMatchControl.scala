package de.knockoutwhist.control.text

import de.knockoutwhist.cards.Player
import de.knockoutwhist.control.{MatchControl, PlayerControl}
import de.knockoutwhist.rounds.{Round, Trick}

object TextMatchControl extends MatchControl {

  override def initial(): Boolean = {
    println("Welcome to Knockout Whist!")
    start()
    true
  }

  override def start(): Unit = {
    
  }

  override def playerControl: PlayerControl = {
    TextPlayerControl
  }

  override def nextRound(): Round = {
    println("Starting the next round.")
    null
  }

  override def nextTrick(): Trick = {
    println("Starting the next trick.")
    null
  }



}

