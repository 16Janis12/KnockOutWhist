package de.knockoutwhist.player

class AIPlayerBuilder extends PlayerBuilder {
  private var unfinished: Option[AIPlayer] = None

  override def setName(name: String): PlayerBuilder = {
    if (unfinished.isEmpty) {
      unfinished = Some(AIPlayer(name))
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
