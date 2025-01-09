package de.knockoutwhist.utils.baseQueue

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue

trait QueueBuilder {

  def setStart(start: Int): QueueBuilder
  def setPlayer(players: Array[AbstractPlayer]): QueueBuilder
  def build(): CustomPlayerQueue[AbstractPlayer]
  
}
