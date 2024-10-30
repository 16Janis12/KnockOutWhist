package de.knockoutwhist.control.text

import de.knockoutwhist.control.{MatchControl, PlayerControl}
import de.knockoutwhist.rounds.{Match, Round, Trick}

object TextMatchControl extends MatchControl {

  override def initial(): Boolean = {
    println("Welcome to Knockout Whist!")
    start()
    true
  }

  override def start(): Unit = {
    while(true) { //Main Gameplay Loop
      
    }
  }

  override def playerControl: PlayerControl = {
    TextPlayerControl
  }

  override def nextRound(matchImpl: Match): Round = {
    if(matchImpl.isOver) {
      println(s"The match is over. The winner is ${matchImpl.finalizeMatch().name}.")
      return null
    }
    matchImpl.create_round()
  }

  override def nextTrick(roundImpl: Round): Trick = {
    if(roundImpl.isOver) {
      println("The round is over.")
      return null
    }
    roundImpl.create_trick()
  }



}

