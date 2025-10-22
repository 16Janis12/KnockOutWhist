package de.knockoutwhist.persistence.formats

import de.knockoutwhist.cards.Card

object CardFormatUtil {

  def grabSpecificCard(card: Card, cc: List[Card]): Card = {
    cc.filter(c => c.suit == card.suit && c.cardValue == card.cardValue).head
  }

}
