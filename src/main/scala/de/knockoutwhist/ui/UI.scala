package de.knockoutwhist.ui

import de.knockoutwhist.control.GameLogic

trait UI {
  
  def initial(gameLogic: GameLogic): Boolean

}
