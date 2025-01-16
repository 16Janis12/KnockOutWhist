package de.knockoutwhist.persistence.formats

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.CardValue.Ten
import de.knockoutwhist.cards.{Card, CardManager, CardValue, Hand, Suit}
import de.knockoutwhist.events.ui.GameState
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint}
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Match, Round, Trick}

import scala.xml.{Elem, Node}
import de.knockoutwhist.utils.Implicits.*

import java.util.UUID
import scala.collection.immutable.HashMap

class XMLFormatter extends FileFormatter {
  override def formatName: String = "XML"

  override def fileEnding: String = "xml"

  override def createFormat(matchSnapshot: MatchSnapshot): Array[Byte] = {
    val xml =
      <gamesave>
        <gameState>{matchSnapshot.gameState.toString}</gameState>
        {matchSnapshot.methodEntryPoint.isDefined ? <methodEntryPoint>{matchSnapshot.methodEntryPoint.get.toString}</methodEntryPoint> |: ""}
        {matchSnapshot.matchImpl.isDefined ? CardManagerXMLFormatter.createCardManagerXML(matchSnapshot.matchImpl.get.cardManager) |: ""}
        {matchSnapshot.matchImpl.isDefined ? MatchXMLFormatter.createMatchXML(matchSnapshot.matchImpl.get) |: ""}
        {matchSnapshot.round.isDefined ? RoundXMLFormatter.createRoundXML(matchSnapshot.round.get) |: ""}
        {matchSnapshot.trick.isDefined ? TrickXMLFormatter.createTrickXML(matchSnapshot.trick.get) |: ""}
        {matchSnapshot.currentIndex.isDefined ? <currentIndex>{matchSnapshot.currentIndex.get}</currentIndex> |: ""}
      </gamesave>
    xml.toString().getBytes
  }

  override def parseFormat(bytes: Array[Byte]): MatchSnapshot = {
    val xml = scala.xml.XML.loadString(new String(bytes))
    val gameState = GameState.valueOf((xml \ "gameState").text)
    val methodEntryPoint = if ((xml \ "methodEntryPoint").isEmpty) {
      None
    } else {
      Some(MethodEntryPoint.valueOf((xml \ "methodEntryPoint").text))
    }
    val playerHelp = PlayerUtil()
    val cardManager = if ((xml \ "cardManager").isEmpty) {
      KnockOutWhist.config.cardManager
    } else {
      CardManagerXMLFormatter.parseCardManagerXML((xml \ "cardManager").head)
    }
    val matchImpl = if ((xml \ "match").isEmpty) {
      None
    } else {
      Some(MatchXMLFormatter.parseMatchXML((xml \ "match").head, playerHelp, cardManager))
    }
    val round = if ((xml \ "round").isEmpty) {
      None
    } else {
      Some(RoundXMLFormatter.parseRoundXML((xml \ "round").head, playerHelp, cardManager))
    }
    val trick = if ((xml \ "trick").isEmpty) {
      None
    } else {
      Some(TrickXMLFormatter.parseTrickXML((xml \ "trick").head, playerHelp, cardManager))
    }
    val currentIndex = if ((xml \ "currentIndex").isEmpty) {
      None
    } else {
      Some((xml \ "currentIndex").text.toInt)
    }
    MatchSnapshot(matchImpl, round, trick, currentIndex, gameState, methodEntryPoint)
  }


  private object PlayerXMLFormatter {
    def createPlayerXML(abstractPlayer: AbstractPlayer): Elem = {
      <player>
        <id>{abstractPlayer.id.toString}</id>
        <name>{abstractPlayer.name}</name>
        {createHandXML(abstractPlayer.hand)}
        <doglife>{abstractPlayer.doglife}</doglife>
        <playerType>{PlayerFactory.parsePlayerType(abstractPlayer).toString}</playerType>
      </player>
    }

    private def createHandXML(hand: Option[Hand]): Elem = {
      if (hand.isDefined) {
        <hand>
          {hand.get.cards.map(card => CardXMLFormatter.createCardXML(card))}
        </hand>
      } else {
        <hand></hand>
      }
    }

    def parsePlayerXML(playerXML: Node, playerUtil: PlayerUtil, cardManager: CardManager): AbstractPlayer = {
      val id = UUID.fromString((playerXML \ "id").text)
      val name = (playerXML \ "name").text
      val handXML = playerXML \ "hand"
      val doglife = (playerXML \ "doglife").text.toBoolean
      val playerType = Playertype.valueOf((playerXML \ "playerType").text)

      val hand = if (handXML.text.isEmpty) {
        None
      } else {
        Some(parseHandXML(handXML.head, cardManager))
      }

      playerUtil.handlePlayer(id, name, hand, doglife, playerType)
    }

    private def parseHandXML(handXML: Node, cardManager: CardManager): Hand = {
      val cards = (handXML \ "card").map(cardXML => {
        val card = CardXMLFormatter.parseCardXML(cardXML)
        cardManager.grabSpecificCard(card)
      })
      Hand(cards.toList)
    }

  }

  private object TrickXMLFormatter {
    def createTrickXML(trick: Trick): Elem = {
      <trick>
        <plays>
          {trick.cards.map { case (card, player) => <play>{CardXMLFormatter.createCardXML(card)}{PlayerXMLFormatter.createPlayerXML(player)}</play> }}
        </plays>
        <firstCard>{(trick.firstCard.isDefined) ? CardXMLFormatter.createCardXML(trick.firstCard.get) |: ""}</firstCard>
        <winner>{(trick.winner != null) ? PlayerXMLFormatter.createPlayerXML(trick.winner) |: ""}</winner>
        <finished>{trick.finished}</finished>
      </trick>
    }

    def parseTrickXML(trickXML: Node, playerUtil: PlayerUtil, cardManager: CardManager): Trick = {
      val plays = (trickXML \ "plays").head
      val tricks = (plays \ "play").map(playXML => {
        val card = CardXMLFormatter.parseCardXML((playXML \ "card").head)
        val player = PlayerXMLFormatter.parsePlayerXML((playXML \ "player").head, playerUtil, cardManager)
        card -> player
      }).groupBy(_._1).map((card, list) => (card, list.map(_._2).head))
      val firstCard = if ((trickXML \ "firstCard").text.isEmpty) {
        None
      } else {
        val c = CardXMLFormatter.parseCardXML((trickXML \ "firstCard" \ "card").head)
        Some(cardManager.grabSpecificCard(c))
      }
      val winner = if ((trickXML \ "winner").text.isEmpty) {
        null
      } else {
        PlayerXMLFormatter.parsePlayerXML((trickXML \ "winner" \ "player").head, playerUtil, cardManager)
      }
      val finished = (trickXML \ "finished").text.toBoolean
      val hashed = HashMap(tricks.toSeq*)
      Trick(hashed, winner, finished, firstCard)
    }
  }

  private object RoundXMLFormatter {

    def createRoundXML(round: Round): Elem = {
      <round>
        <tricks>
          {round.tricklist.map(trick => TrickXMLFormatter.createTrickXML(trick))}
        </tricks>
        <playersin>
          {round.playersin.map(player => PlayerXMLFormatter.createPlayerXML(player))}
        </playersin>
        <playersout>
          {(round.playersout != null) ? round.playersout.map(player => PlayerXMLFormatter.createPlayerXML(player)) |: ""}
        </playersout>
        <trumpSuit>{round.trumpSuit.ordinal}</trumpSuit>
        <startingPlayer>{round.startingPlayer}</startingPlayer>
        <winner>{(round.winner != null) ? PlayerXMLFormatter.createPlayerXML(round.winner) |: ""}</winner>
        <firstRound>{round.firstRound}</firstRound>
      </round>
    }

    def parseRoundXML(roundXML: Node, playerUtil: PlayerUtil, cardManager: CardManager): Round = {
      val tricks = (roundXML \ "tricks").head
      val tricklist = tricks \ "trick"
      val tricksList = tricklist.map(trickXML => TrickXMLFormatter.parseTrickXML(trickXML, playerUtil, cardManager)).toList
      val playersin = (roundXML \ "playersin").head \ "player"
      val playersInList = playersin.map(playerXML => PlayerXMLFormatter.parsePlayerXML(playerXML, playerUtil, cardManager)).toList
      val playersout = (roundXML \ "playersout").head \ "player"
      val playersOutList = playersout.map(playerXML => PlayerXMLFormatter.parsePlayerXML(playerXML, playerUtil, cardManager)).toList
      val trumpSuit = Suit.fromOrdinal((roundXML \ "trumpSuit").text.toInt)
      val startingPlayer = (roundXML \ "startingPlayer").text.toInt
      val winner = if ((roundXML \ "winner").text.isEmpty) {
        null
      } else {
        PlayerXMLFormatter.parsePlayerXML((roundXML \ "winner" \ "player").head, playerUtil, cardManager)
      }
      val firstRound = (roundXML \ "firstRound").text.toBoolean
      Round(trumpSuit, tricksList, playersInList, playersOutList, startingPlayer, winner, firstRound)
    }

  }

  private object MatchXMLFormatter {

    def createMatchXML(matchImpl: Match): Elem = {
      <match>
        <totalplayers>
          {matchImpl.totalplayers.map(player => PlayerXMLFormatter.createPlayerXML(player))}
        </totalplayers>
        <numberofcards>{matchImpl.numberofcards}</numberofcards>
        <dogLife>{matchImpl.dogLife}</dogLife>
        <roundlist>
          {matchImpl.roundlist.map(round => RoundXMLFormatter.createRoundXML(round))}
        </roundlist>
      </match>
    }

    def parseMatchXML(matchXML: Node, playerUtil: PlayerUtil, cardManager: CardManager): Match = {
      val totalplayers = (matchXML \ "totalplayers").head \ "player"
      val totalPlayersList = totalplayers.map(playerXML => PlayerXMLFormatter.parsePlayerXML(playerXML, playerUtil, cardManager)).toList
      val numberofcards = (matchXML \ "numberofcards").text.toInt
      val dogLife = (matchXML \ "dogLife").text.toBoolean
      val roundlist = (matchXML \ "roundlist").head \ "round"
      val roundList = roundlist.map(roundXML => RoundXMLFormatter.parseRoundXML(roundXML, playerUtil, cardManager)).toList
      Match(totalPlayersList, numberofcards, dogLife, roundList, cardManager)
    }

  }

  private object CardXMLFormatter {

    def createCardXML(card: Card): Elem = {
      <card>
        <value>{card.cardValue.ordinal}</value>
        <suit>{card.suit.ordinal}</suit>
      </card>
    }

    def parseCardXML(cardXML: Node): Card = {
      val value = (cardXML \ "value").text.toInt
      val suit = (cardXML \ "suit").text.toInt
      Card(CardValue.fromOrdinal(value), Suit.fromOrdinal(suit))
    }

  }

  private object CardManagerXMLFormatter {
    def createCardManagerXML(cardManager: CardManager): Elem = {
      <cardManager>
        <cardContainer>
          {cardManager.cardContainer.map(card => CardXMLFormatter.createCardXML(card))}
        </cardContainer>
        <currentIdx>{cardManager.currentIndx}</currentIdx>
      </cardManager>
    }

    def parseCardManagerXML(cardManagerXML: Node): CardManager = {
      val cardContainer = (cardManagerXML \ "cardContainer").head \ "card"
      val cardContainerList = cardContainer.map(cardXML => CardXMLFormatter.parseCardXML(cardXML)).toList
      val currentIdx = (cardManagerXML \ "currentIdx").text.toInt
      val cardManager = KnockOutWhist.config.cardManager
      cardManager.setState(cardContainerList, currentIdx)
      cardManager
    }

  }

}
