package de.knockoutwhist.cards

case class Player(name: String) {
  private var hand: Option[Hand] = None
  var doglife: Boolean = false
  def provideHand(hand: Hand): Boolean = {
    this.hand = Some(hand)
    true
  }
  def pickTrumpsuit(): Suit = {
    
    Suit.Spades
    
    
    
  }
  
  
  
  
}
