package de.knockoutwhist.player

trait PlayerBuilder {
  def reset(): PlayerBuilder
  def setName(name:String): PlayerBuilder
  def build(): AbstractPlayer
  
}
