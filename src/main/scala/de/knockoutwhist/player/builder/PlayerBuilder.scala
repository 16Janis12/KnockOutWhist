package de.knockoutwhist.player.builder

import de.knockoutwhist.player.AbstractPlayer

trait PlayerBuilder {
  def reset(): PlayerBuilder
  def setName(name:String): PlayerBuilder
  def build(): AbstractPlayer
  
}
