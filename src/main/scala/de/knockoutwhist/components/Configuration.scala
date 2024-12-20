package de.knockoutwhist.components

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.control.*
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.utils.CustomPlayerQueue

trait Configuration {
  def maincomponent: Maincomponent
  def matchcomponent: Matchcomponent
  def playeractrcomponent: Playeractrcomponent
  def playerlogcomponent: Playerlogcomponent 
  def roundlogcomponent: Roundlogcomponent 
  def trickcomponent: Tricklogcomponent 
  def cardManager: CardManager
  
  def createRightQueue(players: Array[AbstractPlayer], start: Int = 0): CustomPlayerQueue[AbstractPlayer]
  
}