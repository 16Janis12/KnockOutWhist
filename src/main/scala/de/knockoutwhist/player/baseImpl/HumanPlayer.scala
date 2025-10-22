package de.knockoutwhist.player.baseImpl

import de.knockoutwhist.cards.{Card, Hand, Suit}
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try


class HumanPlayer (name: String, id: UUID = UUID.randomUUID()) extends AbstractPlayer(name, id) {
  
}
