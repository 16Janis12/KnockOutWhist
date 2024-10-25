import de.knockoutwhist.cards.CardManager

val originalDeck = List(CardManager.cardContainer)
CardManager.shuffleAndReset()
val shuffledDeck = CardManager.cardContainer