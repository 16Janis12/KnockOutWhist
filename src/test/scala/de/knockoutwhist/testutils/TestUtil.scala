package de.knockoutwhist.testutils

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.utils.DelayHandler

import java.io.{ByteArrayInputStream, OutputStream}


object TestUtil {
  
  def simulateInput[T](input: String)(block: => T): T = {
    Console.withIn(new ByteArrayInputStream(input.getBytes())) {
      block
    }
  }

  def cancelOut[T]()(block: => T): T = {
    Console.withOut((b: Int) => {}) {
      block
    }
  }
  
  def enableDebugMode(): Unit = {
    KnockOutWhist.DEBUG_MODE_VAR = true
  }
  
  def disableDebugMode(): Unit = {
    KnockOutWhist.DEBUG_MODE_VAR = false
  }
  
  def disableDelay(): Unit = {
    DelayHandler.activateDelay = false
  }

}
