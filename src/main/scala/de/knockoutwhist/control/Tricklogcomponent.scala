package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.util.Try

trait Tricklogcomponent {

  def controlSuitplayed(card: Try[Card], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit

  def controlDogPlayed(card: Try[Option[Card]], matchImpl: Match, round: Round, trick: Trick, currentIndex: Int, player: AbstractPlayer): Unit

  def alternativeCards(card: Card, round: Round, trick: Trick, player: AbstractPlayer): List[Card]

  def wonTrick(trick: Trick, round: Round): (AbstractPlayer, Trick)
}
