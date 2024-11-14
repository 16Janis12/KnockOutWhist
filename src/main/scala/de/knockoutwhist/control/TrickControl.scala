package de.knockoutwhist.control

import de.knockoutwhist.cards.Card
import de.knockoutwhist.rounds.{Round, Trick}
import de.knockoutwhist.player.Player

object TrickControl {

  private[control]def playCard(trick: Trick, round: Round,card: Card, player: Player): Boolean = {
    if (trick.finished) {
      throw new IllegalStateException("This trick is already finished")
    } else {
      if (trick.get_first_card().isEmpty) {
        trick.set_first_card(card)
        trick.cards += (card -> player)
        true
      } else if (card.suit == trick.get_first_card().getOrElse(card).suit) { // Wert aus Option extrahieren
        trick.cards += (card -> player)
        true
      } else if (card.suit == round.trumpSuit) {
        trick.cards += (card -> player)
        true
      } else {
        trick.cards += (card -> player)
        false
      }
    }
  }

  def wonTrick(trick: Trick, round: Round): (Player, Trick) = {
    val winningCard = {
      if (trick.cards.keys.exists(_.suit == round.trumpSuit)) {
        trick.cards.keys.filter(_.suit == round.trumpSuit).maxBy(_.cardValue.ordinal) //stream
      } else {
        trick.cards.keys.filter(_.suit == trick.get_first_card().get.suit).maxBy(_.cardValue.ordinal) //stream
      }
    }
    val winningPlayer = trick.cards(winningCard)
    val finalTrick = Trick(round, trick.cards, winningPlayer, true)
    round.tricklist += finalTrick
    (winningPlayer, finalTrick)
  }

}
