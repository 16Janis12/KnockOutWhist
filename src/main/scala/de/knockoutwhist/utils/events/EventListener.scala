package de.knockoutwhist.utils.events

enum Priority extends Ordered[Priority] :
  case High, Normal, Low

  override def compare(that: Priority): Int = {
    if this == that then 0
    else if this == High then 1
    else if that == High then -1
    else if this == Low then -1
    else 1
  }
end Priority


trait EventListener extends Ordered[EventListener] {
  def priority: Priority = Priority.Normal
  def listen[R](event: ReturnableEvent[R]): Option[R]

  override def compare(that: EventListener): Int = that.priority.compare(priority)
}
