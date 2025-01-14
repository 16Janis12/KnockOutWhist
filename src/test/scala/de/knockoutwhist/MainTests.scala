package de.knockoutwhist

import de.knockoutwhist.control.ControlThread
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.tui.TUIMain
import org.scalatest.funsuite.AnyFunSuite

class MainTests extends AnyFunSuite {

  test("Main should be able to go to the main menu") {
    TestUtil.disableDelay()
    TUIMain.init = false
    TestUtil.simulateInput("2\n") {
        KnockOutWhist.main(Array())
    }
  }

  test("Main should be able to be executed twice") {
    TestUtil.disableDelay()
    TestUtil.simulateInput("2\n") {
      assertThrows[IllegalThreadStateException] { // Changed to IllegalThreadState
        KnockOutWhist.main(Array())
      }
    }
  }
  test("DebugMode should be false to start with") {
    val debug = KnockOutWhist.debugmode
    assert(!debug)
  }

}
