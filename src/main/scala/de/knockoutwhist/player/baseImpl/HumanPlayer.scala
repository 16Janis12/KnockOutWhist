package de.knockoutwhist.player.baseImpl

import de.knockoutwhist.player.AbstractPlayer

import java.util.UUID


class HumanPlayer (name: String, id: UUID = UUID.randomUUID()) extends AbstractPlayer(name, id) {
  
}
