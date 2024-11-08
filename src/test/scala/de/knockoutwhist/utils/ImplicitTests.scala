package de.knockoutwhist.utils

import de.knockoutwhist.utils.Implicits.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ImplicitTests extends AnyWordSpec with Matchers {
  "The ternary operator" should {
    "return the left if the condition is met" in {
      1 should be (true ? 1 |: 2)
    }
    "return the right if the condition is not met" in {
      2 should be (false ? 1 |: 2)
    }
    "return None if the condition is not met" in {
      None should be (false ? 1)
    }
    "return an Option if the condition is met" in {
      Some(1) should be (true ? 1)
    }
  }
}
