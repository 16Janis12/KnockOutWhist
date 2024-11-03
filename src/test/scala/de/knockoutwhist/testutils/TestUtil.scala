package de.knockoutwhist.testutils

import de.knockoutwhist.KnockOutWhist

import java.io.{ByteArrayInputStream, InputStream}


object TestUtil {

  def simulateInput(input: String): Unit = {
    val in = new java.io.ByteArrayInputStream(input.getBytes)
    System.setIn(in)
  }
  
  def enableDebugMode(): Unit = {
    KnockOutWhist.DEBUG_MODE_VAR = true
  }

}
