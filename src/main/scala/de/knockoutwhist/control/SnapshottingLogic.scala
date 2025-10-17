package de.knockoutwhist.control

trait SnapshottingGameLogic {
  
  def createSnapshot(): LogicSnapshot[this.type]

}

trait LogicSnapshot[T <: SnapshottingGameLogic] {
  
  def restore(logic: T): Unit
  
}
