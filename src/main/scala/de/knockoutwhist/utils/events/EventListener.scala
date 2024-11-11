package de.knockoutwhist.utils.events

trait EventListener {
  
  def listen(event: Event): Boolean

}
