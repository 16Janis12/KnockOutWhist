package de.knockoutwhist.player.builder

import de.knockoutwhist.player.AbstractPlayer

import scala.util.Random

object Director {

  private val playernames = List("Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy", "Mallory", "Oscar", "Peggy", "Trent", "Walter")

  def constructWithRandomNames(playerBuilder: PlayerBuilder): AbstractPlayer = {
    playerBuilder.reset()
    playerBuilder.setName(playernames(Random.nextInt(playernames.length)))
    playerBuilder.build()
  }

  def constructWithName(playerBuilder: PlayerBuilder, name: String): AbstractPlayer = {
    playerBuilder.reset()
    playerBuilder.setName(name)
    playerBuilder.build()
  }

}
