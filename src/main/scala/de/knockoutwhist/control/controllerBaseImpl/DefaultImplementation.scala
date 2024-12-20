package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.{Maincomponent, Matchcomponent, Playeractrcomponent, Playerlogcomponent, Roundlogcomponent, Tricklogcomponent}

object DefaultImplementation extends Configuration {
  def maincomponent: Maincomponent = MainLogic
  def matchcomponent: Matchcomponent = MatchLogic
  def playeractrcomponent: Playeractrcomponent = PlayerControl
  def playerlogcomponent: Playerlogcomponent = PlayerLogic
  def roundlogcomponent: Roundlogcomponent = RoundLogic
  def trickcomponent: Tricklogcomponent = TrickLogic
  def cardManager: CardManager = CardBaseManager
}
