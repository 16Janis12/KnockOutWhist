package de.knockoutwhist.testutils

import de.knockoutwhist.KnockOutWhist

import java.io.ByteArrayInputStream


object TestUtil {
  
  def simulateInput[T](input: String)(block: => T): T = {
    Console.withIn(new ByteArrayInputStream(input.getBytes())) {
      block
    }
  }

  def cancelOut[T]()(block: => T): T = {
    Console.withOut(new java.io.ByteArrayOutputStream()) {
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
