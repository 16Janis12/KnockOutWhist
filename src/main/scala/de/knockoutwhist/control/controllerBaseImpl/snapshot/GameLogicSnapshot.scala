package de.knockoutwhist.control.controllerBaseImpl.snapshot

import de.knockoutwhist.rounds.{Match, Round, Trick}

case class GameLogicSnapshot(
                            matchImpl: Option[Match],
                            roundImpl: Option[Round],
                            trickImpl: Option[Trick],
                            
                            
                            
                            )
