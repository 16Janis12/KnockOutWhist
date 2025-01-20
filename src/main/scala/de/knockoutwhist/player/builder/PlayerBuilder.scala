package de.knockoutwhist.player.builder

import de.knockoutwhist.player.AbstractPlayer

import java.util.UUID

trait PlayerBuilder {
  def reset(): PlayerBuilder
  def setName(name:String): PlayerBuilder
  def setID(id: UUID): PlayerBuilder
  def build(): AbstractPlayer
  
}
