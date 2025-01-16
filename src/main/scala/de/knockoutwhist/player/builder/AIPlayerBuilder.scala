package de.knockoutwhist.player.builder

import de.knockoutwhist.player.{AIPlayer, AbstractPlayer}

import java.util.UUID

class AIPlayerBuilder extends PlayerBuilder {
  private var name: Option[String] = None
  private var id: Option[UUID] = Some(UUID.randomUUID())

  override def setName(name: String): PlayerBuilder = {
    this.name = Some(name)
    this
  }

  override def setID(id: UUID): PlayerBuilder = {
    this.id = Some(id)
    this
  }

  override def reset(): PlayerBuilder = {
    this.name = None
    this.id = Some(UUID.randomUUID())
    this
  }

  override def build(): AbstractPlayer = {
    if (this.name.isDefined && this.id.isDefined) {
      val player = new AIPlayer(this.name.get, None, id.get, false)
      reset()
      return player
    }
    throw new IllegalStateException("Trying to build non-existing AI")
  }

}
