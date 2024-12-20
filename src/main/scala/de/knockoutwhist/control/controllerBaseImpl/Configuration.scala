package de.knockoutwhist.control.controllerBaseImpl

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.cards.base.CardBaseManager
import de.knockoutwhist.control.{Maincomponent, Matchcomponent, Playeractrcomponent, Playerlogcomponent, Roundlogcomponent, Tricklogcomponent}

trait Configuration {
  def maincomponent: Maincomponent
  def matchcomponent: Matchcomponent
  def playeractrcomponent: Playeractrcomponent
  def playerlogcomponent: Playerlogcomponent 
  def roundlogcomponent: Roundlogcomponent 
  def trickcomponent: Tricklogcomponent 
  def cardManager: CardManager 
}