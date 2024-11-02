package de.knockoutwhist.testutils

import java.io.{ByteArrayInputStream, InputStream}


object TestUtil {

  def simulateInput(input: String): Unit = {
    val in = new java.io.ByteArrayInputStream(input.getBytes)
    System.setIn(in)
  }

}
