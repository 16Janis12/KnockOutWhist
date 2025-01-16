package de.knockoutwhist.persistence.formats

import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.cards.{Card, CardManager, CardValue, Hand, Suit}
import de.knockoutwhist.persistence.MatchSnapshot
import de.knockoutwhist.player.AbstractPlayer

import scala.xml.Elem
import de.knockoutwhist.utils.Implicits.*

class XMLFormatter extends FileFormatter {
  override def formatName: String = "XML"

  override def fileEnding: String = "xml"

  override def createFormat(matchSnapshot: MatchSnapshot): Array[Byte] = {
    val data = <gamesave></gamesave>
    data.toString.getBytes
  }

  override def parseFormat(bytes: Array[Byte]): MatchSnapshot = ???


  object PlayerXMLFormatter {
    def createPlayerXML(abstractPlayer: AbstractPlayer): Elem = {
      <player>
        <id>{abstractPlayer.id.toString}</id>
        <name>{abstractPlayer.name}</name>
        {createHandXML(abstractPlayer.hand)}
        <doglife>{abstractPlayer.doglife}</doglife>
        <playerType>{abstractPlayer.getClass.getSimpleName}</playerType>
      </player>
    }

    def createHandXML(hand: Option[Hand]): Elem = {
      if (hand.isDefined) {
        <hand>
          {hand.get.cards.map(card => CardXMLFormatter.createCardXML(card))}
        </hand>
      } else {
        <hand></hand>
      }
    }

  }

  object CardXMLFormatter {

    def createCardXML(card: Card): Elem = {
      <card>
        <value>{card.cardValue.ordinal}</value>
        <suit>{card.suit.ordinal}</suit>
      </card>
    }

    def parseCardXML(cardXML: Elem): Card = {
      val value = (cardXML \ "value").text.toInt
      val suit = (cardXML \ "suit").text.toInt
      Card(CardValue.fromOrdinal(value), Suit.fromOrdinal(suit))
    }

  }


}
