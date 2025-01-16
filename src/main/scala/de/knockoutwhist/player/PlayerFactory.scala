package de.knockoutwhist.player

import de.knockoutwhist.player.Playertype.{AI, HUMAN, STUB}
import de.knockoutwhist.player.baseImpl.HumanPlayer
import de.knockoutwhist.player.builder.*

import java.util.UUID

enum Playertype:
  case HUMAN
  case AI
  case STUB
end Playertype

object PlayerFactory {
  def createPlayer(name: String = null, id: UUID = null, playertype: Playertype): AbstractPlayer = {
    val buildType: PlayerBuilder = playertype match {
      case HUMAN =>
        new HumanoidBuilder()
      case AI =>
        new AIPlayerBuilder()
      case STUB =>
        new StubPlayerBuilder
    }
    if (name == null) {
      if (id != null) {
        Director.constructWithRandomNamesAndID(buildType, id)
      } else {
        Director.constructWithRandomNames(buildType)
      }
    } else {
      if (id != null) {
        Director.constructWithNameAndID(buildType, name, id)
      } else {
        Director.constructWithName(buildType, name)
      }
    }
  }
  
  def parsePlayerType(player: AbstractPlayer): Playertype = {
    player match {
      case _: HumanPlayer =>
        HUMAN
      case _: AIPlayer =>
        AI
      case _: StubPlayer =>
        STUB
    }
  }

}
