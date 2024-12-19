package de.knockoutwhist.player.builder

import de.knockoutwhist.player.{MockPlayer, AbstractPlayer}

class MockPlayerBuilder extends PlayerBuilder {
  private var unfinished: Option[MockPlayer] = None

  override def setName(name: String): PlayerBuilder = {
    if (unfinished.isEmpty) {
      unfinished = Some(MockPlayer(name, None))
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
