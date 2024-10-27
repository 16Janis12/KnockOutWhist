package de.knockoutwhist.cards

import java.io.{ByteArrayInputStream, InputStream}


object TestUtil {

  def simulateInput(input: String): Option[SimulatedIn] = {
    val oldIn = System.in
    if(oldIn.isInstanceOf[ByteArrayInputStream]) {
      return None
    }
    val in = new java.io.ByteArrayInputStream(input.getBytes)
    System.setIn(in)
    Some(SimulatedIn(oldIn))
  }

  class SimulatedIn (inputStream: InputStream) {
    def close(): Unit = System.setIn(inputStream)
  }

}
