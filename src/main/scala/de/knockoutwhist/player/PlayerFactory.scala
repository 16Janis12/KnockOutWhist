package de.knockoutwhist.player

import de.knockoutwhist.player.Playertype.{AI, HUMAN}

enum Playertype:
  case HUMAN
  case AI
end Playertype

object PlayerFactory {
  def createPlayer(name: String, playertype: Playertype): AbstractPlayer = {
    playertype match
      case HUMAN => HumanPlayer(name)
      case AI => AIPlayer(s"$name - AI")
  }
}
