package de.knockoutwhist.cards

enum Suit(identifier: String):
  case Spades extends Suit("♠")
  case Hearts extends Suit("♥")
  case Diamonds extends Suit("♦")
  case Clubs extends Suit("♣")
end Suit

enum CardValue(identifier: String):
  case Two extends CardValue("2")
  case Three extends CardValue("3")
  case Four extends CardValue("4")
  case Five extends CardValue("5")
  case Six extends CardValue("6")
  case Seven extends CardValue("7")
  case Eight extends CardValue("8")
  case Nine extends CardValue("9")
  case Ten extends CardValue("10")
  case Jack extends CardValue("J")
  case Queen extends CardValue("Q")
  case King extends CardValue("K")
  case Ace extends CardValue("A")
end CardValue

case class Card(cardValue: CardValue, suit: Suit) {
  override def toString: String = s"$cardValue of $suit"

}
