package de.knockoutwhist.control.controllerBaseImpl.sublogic

import de.knockoutwhist.cards.{Card, Suit}
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogic
import de.knockoutwhist.control.controllerBaseImpl.sublogic.util.RoundUtil
import de.knockoutwhist.control.sublogic.PlayerInputLogic
import de.knockoutwhist.events.player.{PlayCardEvent, RequestTrumpSuitEvent}
import de.knockoutwhist.player.AbstractPlayer

final class BasePlayerInputLogic(gameLogic: BaseGameLogic) extends PlayerInputLogic {
  
  override def requestTrumpSuit(player: AbstractPlayer): Unit = {
    gameLogic.invoke(RequestTrumpSuitEvent(player))
  }
  
  override def receivedTrumpSuit(suit: Suit): Unit = {
    val newRound = RoundUtil.createRound(suit)
    gameLogic.currentRound = Some(newRound)
    gameLogic.controlRound()
  }
  
  override def requestCard(player: AbstractPlayer): Unit = {
    gameLogic.invoke(PlayCardEvent(player))
  }
  
  override def receivedCard(card: Card): Unit = {
    if (gameLogic.currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = gameLogic.currentTrick.get
    if (gameLogic.currentPlayer.isEmpty) throw new IllegalStateException("No current player set")
    val player = gameLogic.currentPlayer.get

    val newTrick = if (trickImpl.firstCard.isEmpty) {
       trickImpl
        .setfirstcard(card)
        .addCard(card, player)
    } else {
       trickImpl
        .addCard(card, player)
    }
    player.removeCard(card)
    
    gameLogic.currentTrick = Some(newTrick)
    gameLogic.controlTrick()
  }
  
  override def receivedDog(dog: Option[Card]): Unit = {
    if (gameLogic.currentTrick.isEmpty) throw new IllegalStateException("No current trick set")
    val trickImpl = gameLogic.currentTrick.get
    if (gameLogic.currentPlayer.isEmpty) throw new IllegalStateException("No current player set")
    val player = gameLogic.currentPlayer.get
    
    if (dog.isDefined) {
      val newTrick = if (trickImpl.firstCard.isEmpty) {
        trickImpl
          .setfirstcard(dog.get)
          .addCard(dog.get, player)
      } else {
        trickImpl
          .addCard(dog.get, player)
      }
      player.removeCard(dog.get)
      gameLogic.currentTrick = Some(newTrick)
    }
    gameLogic.controlTrick()
  }
  
}
