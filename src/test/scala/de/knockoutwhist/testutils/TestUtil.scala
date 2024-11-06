package de.knockoutwhist.testutils

import de.knockoutwhist.KnockOutWhist

import java.io.ByteArrayInputStream


object TestUtil {
  
  def simulateInput[T](input: String)(block: => T): T = {
    Console.withIn(new ByteArrayInputStream(input.getBytes())) {
      block
    }
  }
  
  def enableDebugMode(): Unit = {
    KnockOutWhist.DEBUG_MODE_VAR = true
  }
  
  def disableDebugMode(): Unit = {
    KnockOutWhist.DEBUG_MODE_VAR = false
  }

}
