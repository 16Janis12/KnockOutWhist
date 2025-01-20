package de.knockoutwhist.player.builder

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.player.baseImpl.HumanPlayer

import java.util.UUID
import scala.compiletime.uninitialized

class HumanoidBuilder extends PlayerBuilder {
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
      val player = new HumanPlayer(this.name.get, None, id.get, false)
      reset()
      return player
    }
    println(this.name)
    println(this.id)
    throw new IllegalStateException("Trying to build non-existing HumanPlayer")
  }

}
