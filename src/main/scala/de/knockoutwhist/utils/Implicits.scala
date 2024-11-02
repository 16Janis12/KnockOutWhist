package de.knockoutwhist.utils

import scala.annotation.targetName

object Implicits {

  implicit class Ternable(condition: Boolean) {
    def ?[T](ifTrue: => T): Option[T] = {
      if (condition) Some(ifTrue) else None
    }
  }

  implicit class Colonable[T](ifFalse: => T) {
    def |:(intermediate: Option[T]): T =
      intermediate match {
        case Some(ifTrue) => ifTrue
        case None => ifFalse
      }
  }
  
}
