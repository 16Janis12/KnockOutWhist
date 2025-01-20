package de.knockoutwhist.utils.baseQueue

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue

class CustomPlayerQueueBuilder extends QueueBuilder {

  private var start: Int = -1
  private var players: Option[Array[AbstractPlayer]] = None

  override def setStart(start: Int): QueueBuilder = {
    this.start = start
    this
  }

  override def setPlayer(players: Array[AbstractPlayer]): QueueBuilder = {
    this.players = Some(players)
    this
  }
  
  override def build(): CustomPlayerQueue[AbstractPlayer] = {
    if(start == -1 || players.isEmpty) throw new IllegalStateException("Start or Players not defined")
    new CustomPlayerBaseQueue[AbstractPlayer](players.get, start)
  }

}
