package de.knockoutwhist.player

import de.knockoutwhist.player.Playertype.{AI, HUMAN}
import de.knockoutwhist.player.builder.{AIPlayerBuilder, Director, HumanoidBuilder, PlayerBuilder}

enum Playertype:
  case HUMAN
  case AI
end Playertype

object PlayerFactory {
  def createPlayer(name: String = null, playertype: Playertype): AbstractPlayer = {
    val buildType: PlayerBuilder = playertype match {
      case HUMAN => {
        new HumanoidBuilder()
      }
      case AI => {
        new AIPlayerBuilder()
      }
    }
    if (name == null) {
      Director.constructWithRandomNames(buildType)
    } else {
      Director.constructWithName(buildType, name)
    }
  }

}
