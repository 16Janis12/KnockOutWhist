package de.knockoutwhist.rounds

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.{Card, CardManager, CardValue, Suit}
import de.knockoutwhist.control.text.TextMatchControl
import de.knockoutwhist.player.Player
import de.knockoutwhist.testutils.TestUtil
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.{should, shouldBe}
import org.scalatest.wordspec.AnyWordSpec
import de.knockoutwhist.control.text.TextMatchControl

import scala.List
import scala.collection.mutable.ListBuffer


class RealGameplay extends AnyWordSpec with Matchers {
  "The Match Control" should {
    "throw no exception" in {
      TestUtil.enableDebugMode()
      TestUtil.simulateInput("1\nLeon,Janis\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\n1\ny\n1\n1\n1\n2\n") {
        TextMatchControl.start()
      }

    }
  }


}
