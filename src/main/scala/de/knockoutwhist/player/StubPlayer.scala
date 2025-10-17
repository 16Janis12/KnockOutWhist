package de.knockoutwhist.player

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.cards.Suit.Spades
import de.knockoutwhist.cards.{Card, Hand}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import java.util.UUID
import scala.collection.immutable
import scala.util.Try

class StubPlayer private[player](name: String, id: UUID = UUID.randomUUID()) extends AbstractPlayer(name, id) {
  
}
