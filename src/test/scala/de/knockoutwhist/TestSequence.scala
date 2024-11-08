package de.knockoutwhist

import de.knockoutwhist.cards.{CardTests, DeckTests, HandTests}
import de.knockoutwhist.control.text.{TextMatchControllerTests, TextPlayerControllerTests}
import de.knockoutwhist.player.PlayerTests
import de.knockoutwhist.rounds.{GameplayTests, MatchTests, TrickTests}
import de.knockoutwhist.utils.{ImplicitTests, QueueTests}
import org.scalatest.Sequential

class TestSequence extends Sequential(
  new GameplayTests(),
  new MainTests(),
  new MatchTests(),
  new TrickTests(),
  new QueueTests(),
  new ImplicitTests(),
  new PlayerTests(),
  new TextPlayerControllerTests(),
  new TextMatchControllerTests(),
  new CardTests(),
  new DeckTests(),
  new HandTests(),
  
) {}