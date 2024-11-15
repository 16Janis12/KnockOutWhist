package de.knockoutwhist.rounds

import de.knockoutwhist.cards.CardManager
import de.knockoutwhist.testutils.TestUtil
import de.knockoutwhist.ui.tui.TUIMain
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec

import scala.List


class GameplayTests extends AnyWordSpec with Matchers {
  "The Match Control" must {
    "not throw an exception" in {
      TestUtil.enableDebugMode()
      CardManager.shuffleAndReset()
      CardManager.resetOrder()
      TestUtil.cancelOut() {
        TestUtil.simulateInput("a\n5\n1\nLeon,Janis\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\ny\n1\n1\n1\n2\n") {
          TUIMain.initial
        }
      }
    }
  }


}
