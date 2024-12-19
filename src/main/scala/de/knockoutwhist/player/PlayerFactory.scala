package de.knockoutwhist.player

import de.knockoutwhist.player.Playertype.{AI, HUMAN, MOCK}
import de.knockoutwhist.player.builder.{AIPlayerBuilder, Director, HumanoidBuilder, MockPlayerBuilder, PlayerBuilder}

enum Playertype:
  case HUMAN
  case AI
  case MOCK
end Playertype

object PlayerFactory {
  def createPlayer(name: String = null, playertype: Playertype): AbstractPlayer = {
    val buildType: PlayerBuilder = playertype match {
      case HUMAN =>
        new HumanoidBuilder()
      case AI =>
        new AIPlayerBuilder()
      case MOCK =>
        new MockPlayerBuilder
    }
    if (name == null) {
      Director.constructWithRandomNames(buildType)
    } else {
      Director.constructWithName(buildType, name)
    }
  }

}
