package de.knockoutwhist.persistence.formats

import de.knockoutwhist.cards.{Card, CardValue, Hand, Suit}
import de.knockoutwhist.player.Playertype
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.util.UUID

class FileFormatterPlayerUtilSpec extends AnyWordSpec with Matchers {

  "FileFormatter.PlayerUtil" should {

    "create and reuse player instances and preserve hand/doglife" in {
      val ff = new FileFormatter {
        override def formatName: String = "test"
        override def fileEnding: String = ".test"
        override def createFormat(matchSnapshot: de.knockoutwhist.persistence.MatchSnapshot): Array[Byte] = Array.emptyByteArray
        override def parseFormat(bytes: Array[Byte]): de.knockoutwhist.persistence.MatchSnapshot = throw new UnsupportedOperationException()
      }

      val util = new ff.PlayerUtil()
      val id = UUID.randomUUID()
      val hand = Hand(List(Card(CardValue.Ace, Suit.Spades)))

      val p1 = util.handlePlayer(id, "p1", Some(hand), true, Playertype.STUB)
      p1.currentHand().isDefined shouldBe true
      p1.isInDogLife shouldBe true

      val p2 = util.handlePlayer(id, "p1", None, false, Playertype.STUB)
      // same player instance should be returned (same id)
      p2 shouldBe p1
      // original hand and doglife remain
      p2.currentHand().isDefined shouldBe true
      p2.isInDogLife shouldBe true
    }
  }
}


class CardFormatUtilSpec extends AnyWordSpec with Matchers {

  "CardFormatUtil" should {

    "grab the specific matching card from a container" in {
      val c1 = Card(CardValue.Two, Suit.Spades)
      val c2 = Card(CardValue.Ace, Suit.Hearts)
      val cc = List(c1, c2)

      val found = CardFormatUtil.grabSpecificCard(c2, cc)
      found shouldBe c2
    }

    "throw NoSuchElementException when card not present" in {
      val c1 = Card(CardValue.Two, Suit.Spades)
      val cc = List(c1)

      an [NoSuchElementException] should be thrownBy {
        CardFormatUtil.grabSpecificCard(Card(CardValue.Ace, Suit.Clubs), cc)
      }
    }
  }
}

