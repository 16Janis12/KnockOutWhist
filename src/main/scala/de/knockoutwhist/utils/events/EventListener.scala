package de.knockoutwhist.utils.events

enum Priority extends Ordered[Priority] :
  case High, Normal, Low

  override def compare(that: Priority): Int = {
    that match {
      case High => 1
      case Normal => 0
      case Low => -1
    }
  }
end Priority


trait EventListener extends Ordered[EventListener] {
  def priority: Priority = Priority.Normal
  def listen[R](event: ReturnableEvent[R]): Option[R]

  override def compare(that: EventListener): Int = that.priority.compare(priority)
}
