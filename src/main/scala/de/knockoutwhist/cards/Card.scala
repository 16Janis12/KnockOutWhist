package de.knockoutwhist.cards

enum Suit:
  case Spades
  case Hearts
  case Diamonds
  case Clubs
  
end Suit

enum CardValue:
  case Two
  case Three
  case Four
  case Five
  case Six
  case Seven
  case Eight
  case Nine
  case Ten
  case Jack
  case Queen
  case King
  case Ace

end CardValue

case class Card(cardValue: CardValue, suit: Suit) {

  override def toString: String = s"$cardValue of $suit"
}
