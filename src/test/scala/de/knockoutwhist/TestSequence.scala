package de.knockoutwhist

import de.knockoutwhist.configuration.DefaultConfigurationTests
import de.knockoutwhist.cards.{CardTests, DeckTests, HandTests, StubCardManagerTests}
import de.knockoutwhist.controlold.{MatchControllerTests, PlayerControllerTests}
import de.knockoutwhist.events.TestAllEvent
import de.knockoutwhist.logic.{CtrRoundTests, MainLogicTests, MatchLogicTests}
import de.knockoutwhist.player.builder.BuilderTests
import de.knockoutwhist.player.{AITests, PlayerTests, StubPlayerTests}
import de.knockoutwhist.rounds.{GameplayTests, MatchTests, TrickTests}
import de.knockoutwhist.utils.events.EventTests
import de.knockoutwhist.utils.{ImplicitTests, QueueTests}
import org.scalatest.Sequential

class TestSequence extends Sequential(
  //new GameplayTests(),
  new MainTests(),
  new MatchTests(),
  new TrickTests(),
  new QueueTests(),
  new ImplicitTests(),
  new PlayerTests(),
  new PlayerControllerTests(),
  new MatchControllerTests(),
  new CardTests(),
  new DeckTests(),
  new HandTests(),
  new StubCardManagerTests(),
  new DefaultConfigurationTests(),
  new EventTests(),
  //new TestAllEvent(),
  new DelayHandlerTests(),
  //new AITests(),
  new BuilderTests(),
  new MainLogicTests(),
  new MatchLogicTests(),
  //new StubPlayerTests
  //new CtrRoundTests()
)