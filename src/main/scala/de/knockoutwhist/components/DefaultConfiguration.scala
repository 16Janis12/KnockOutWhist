package de.knockoutwhist.components

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.*
import de.knockoutwhist.control.controllerBaseImpl.*
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.baseQueue.CustomPlayerBaseQueue

object DefaultConfiguration extends Configuration {
  def maincomponent: Maincomponent = MainLogic
  def matchcomponent: Matchcomponent = MatchLogic
  def playeractrcomponent: Playeractrcomponent = PlayerControl
  def playerlogcomponent: Playerlogcomponent = PlayerLogic
  def roundlogcomponent: Roundlogcomponent = RoundLogic
  def trickcomponent: Tricklogcomponent = TrickLogic
  def cardManager: CardManager = CardBaseManager

  override def createRightQueue(players: Array[AbstractPlayer], start: Int): CustomPlayerQueue[AbstractPlayer] = {
    new CustomPlayerBaseQueue(players, start)
  }
}
