package de.knockoutwhist.components

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.*

trait Configuration {
  def maincomponent: Maincomponent
  def matchcomponent: Matchcomponent
  def playeractrcomponent: Playeractrcomponent
  def playerlogcomponent: Playerlogcomponent 
  def roundlogcomponent: Roundlogcomponent 
  def trickcomponent: Tricklogcomponent 
  def cardManager: CardManager
  
  
  
}