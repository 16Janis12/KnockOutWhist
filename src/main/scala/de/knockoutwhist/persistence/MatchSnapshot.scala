package de.knockoutwhist.persistence

import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

case class MatchSnapshot(
                        matchImpl: Option[Match],
                        round: Option[Round],
                        trick: Option[Trick],
                        currentPlayer: Option[AbstractPlayer],
                        
                        )
