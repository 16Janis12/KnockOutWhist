package de.knockoutwhist.cards

case class Player(name: String) {
  private var hand: Option[Hand] = None
  
  def provideHand(hand: Hand): Unit = {
    this.hand = Some(hand)
  }
  
  
  
  
}
