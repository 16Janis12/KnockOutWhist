package de.knockoutwhist.player.builder

import de.knockoutwhist.player.{AbstractPlayer, StubPlayer}

class StubPlayerBuilder extends PlayerBuilder {
  private var unfinished: Option[StubPlayer] = None

  override def setName(name: String): PlayerBuilder = {
    if (unfinished.isEmpty) {
      unfinished = Some(StubPlayer(name, None))
    } else {
      unfinished.get.name = name
    }
    this
  }

  override def reset(): PlayerBuilder = {
    unfinished = None
    this
  }

  override def build(): AbstractPlayer = {
    if (unfinished.isDefined) {
      val player = unfinished.get
      reset()
      return player
    }
    throw new IllegalStateException("Trying to build non-existing AI")
  }

}
