package de.knockoutwhist.player.builder

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class BuilderTests extends AnyWordSpec with Matchers {
  "HumanoidBuilder" should {
    "create a HumanPlayer" in {
      val builder = new HumanoidBuilder()
      val player = builder.setName("Test").build()
      player.name should be("Test")
    }
    "be able change the name" in {
      val builder = new HumanoidBuilder()
      builder.setName("Test")
      val player = builder.setName("Test2").build()
      player.name should be("Test2")
    }
    "throw an exception if build is called without setting the name" in {
      val builder = new HumanoidBuilder()
      assertThrows[IllegalStateException] {
        builder.build()
      }
    }
  }

  "AIPlayerBuilder" should {
    "create an AIPlayer" in {
      val builder = new AIPlayerBuilder()
      val player = builder.setName("Test").build()
      player.name should be("Test")
    }
    "be able change the name" in {
      val builder = new AIPlayerBuilder()
      builder.setName("Test")
      val player = builder.setName("Test2").build()
      player.name should be("Test2")
    }
    "throw an exception if build is called without setting the name" in {
      val builder = new AIPlayerBuilder()
      assertThrows[IllegalStateException] {
        builder.build()
      }
    }
  }
}
