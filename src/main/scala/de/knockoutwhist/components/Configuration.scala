package de.knockoutwhist.components

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.persistence.formats.FileFormatter
import de.knockoutwhist.player.AbstractPlayer
import de.knockoutwhist.ui.UI
import de.knockoutwhist.utils.CustomPlayerQueue
import de.knockoutwhist.utils.events.EventListener

trait Configuration {
  def cardManager: CardManager
  def fileFormatter: FileFormatter
  def uis: Set[UI]
  def listener: Set[EventListener]
  
  def createRightQueue(players: Array[AbstractPlayer], start: Int = 0): CustomPlayerQueue[AbstractPlayer]
  
}