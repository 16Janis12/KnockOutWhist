package de.knockoutwhist.utils.events

trait SimpleEvent extends ReturnableEvent[Boolean] {
  override def result: Boolean = false
}
