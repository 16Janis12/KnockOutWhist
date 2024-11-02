package de.knockoutwhist.utils

import scala.annotation.targetName

object Implicits {
  
  implicit class Ternable(condition: Boolean) {
    def ?[T](ifTrue: => T): Option[T] = {
      if (condition) Some(ifTrue) else None
    }
  }

  implicit class Colonable[T, F](ifFalse: => F) {
    def |:(intermediate: Option[T]): Either[T, F] =
      intermediate match {
        case Some(ifTrue) => Left(ifTrue)
        case None => Right(ifFalse)
      }
  }
  
}
