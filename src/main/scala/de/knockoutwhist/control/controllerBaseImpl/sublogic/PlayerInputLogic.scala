package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.control.controllerBaseImpl.GameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundUtil
import de.knockoutwhist.player.AbstractPlayer

final class PlayerInputLogic(gameLogic: GameLogic) {
  
  
  
  def requestTrumpsuit(player: AbstractPlayer): Unit = {
    //TODO implement request trump suit logic
  }
  
  def receivedTrumpSuit(player: AbstractPlayer, suit: Suit): Unit = {
    val newRound = RoundUtil.createRound(suit)
    gameLogic.currentRound = Some(newRound)
    gameLogic.controlRound()
  }
  
  def requestCard(player: AbstractPlayer): Unit = {
    //TODO implement request card logic
  }
  
  def receivedCard(player: AbstractPlayer, card: Card): Unit = {
    if (gameLogic.currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = gameLogic.currentTrick.get

    val newTrick = if (trickImpl.firstCard.isEmpty) {
       trickImpl
        .setfirstcard(card)
        .addCard(card, player)
    } else {
       trickImpl
        .addCard(card, player)
    }
    
    gameLogic.currentTrick = Some(newTrick)
    gameLogic.controlTrick()
  }
  
  def requestDog(player: AbstractPlayer): Unit = {
    //TODO implement request dog logic
  }
  
  def receivedDog(player: AbstractPlayer, dog: Option[Card]): Unit = {
    if (gameLogic.currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = gameLogic.currentTrick.get
    
    if (dog.isDefined) {
      val newTrick = if (trickImpl.firstCard.isEmpty) {
        trickImpl
          .setfirstcard(dog.get)
          .addCard(dog.get, player)
      } else {
        trickImpl
          .addCard(dog.get, player)
      }
      gameLogic.currentTrick = Some(newTrick)
    }
    gameLogic.controlTrick()
  }
  
}
