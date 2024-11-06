package de.knockoutwhist

import de.knockoutwhist.testutils.TestUtil
import org.scalatest.funsuite.AnyFunSuite

class MainTests extends AnyFunSuite {

  test("Main should be able to go to the main menu") {
    TestUtil.simulateInput("2\n") {
      KnockOutWhist.main(Array())
    }
  }

  test("Main should be able to be executed twice") {
    TestUtil.simulateInput("2\n") {
      assertThrows[IllegalStateException] {
        KnockOutWhist.main(Array())
      }
    }
  }

}
