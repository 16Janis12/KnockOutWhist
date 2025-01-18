package de.knockoutwhist.persistence.formats

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.*
import de.knockoutwhist.events.ui.GameState
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint}
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}
import de.knockoutwhist.rounds.{Match, Round, Trick}
import play.api.libs.json
import play.api.libs.json.*

import java.util.UUID
import scala.collection.immutable.HashMap

class JSONFormatter extends FileFormatter {

  private val playerUtil = new PlayerUtil

  override def formatName: String = "JSON"

  override def fileEnding: String = "json"

  override def createFormat(matchSnapshot: MatchSnapshot): Array[Byte] = {
    val js = Json.obj(
      "gamestate" -> matchSnapshot.gameState.toString,
      matchSnapshot.methodEntryPoint match {
        case None => "methodEntryPoint" -> JsString("None")
        case Some(methodEntryPoint) => "methodEntryPoint" -> JsString(methodEntryPoint.toString)
      },
      matchSnapshot.matchImpl match {
        case None => "match" -> JsString("None")
        case Some(matchImpl) => "match" -> MatchJsonFormatter.createMatchJson(matchImpl)
      },
      matchSnapshot.matchImpl match {
        case None => "cardManager" -> JsString("None")
        case Some(matchImpl) => "cardManager" -> CardManagerJsonFormatter.createCardManagerJson(matchImpl.cardManager)
      },
      matchSnapshot.round match {
        case None => "round" -> JsString("None")
        case Some(round) => "round" -> RoundJsonFormatter.createRoundJson(round)
      },
      matchSnapshot.trick match {
        case None => "trick" -> JsString("None")
        case Some(trick) => "trick" -> TrickJsonFormatter.createTrickJson(trick)
      },
      matchSnapshot.currentIndex match {
        case None => "currentIndex" -> JsString("None")
        case Some(index) => "currentIndex" -> JsNumber(index)
      }
    )
    js.toString().getBytes
  }

  override def parseFormat(bytes: Array[Byte]): MatchSnapshot = {
    val json = Json.parse(new String(bytes))
    val gameState = GameState.valueOf((json \ "gamestate").get.asInstanceOf[JsString].value)
    val methodEntryPoint = (json \ "methodEntryPoint").get match {
      case JsString("None") => None
      case JsString(methodEntryPoint) => Some(MethodEntryPoint.valueOf(methodEntryPoint))
      case default => None
    }
    val matchImpl = (json \ "match").get match {
      case JsString("None") => None
      case matchJson => Some(MatchJsonFormatter.parseMatchJson(matchJson.asInstanceOf[JsObject], playerUtil, CardManagerJsonFormatter.parseCardManagerJson((json \ "cardManager").get.asInstanceOf[JsObject])))
      case default => None
    }
    val round = (json \ "round").get match {
      case JsString("None") => None
      case roundJson => Some(RoundJsonFormatter.parseRoundJson(roundJson.asInstanceOf[JsObject], playerUtil, CardManagerJsonFormatter.parseCardManagerJson((json \ "cardManager").get.asInstanceOf[JsObject])))
      case default => None
    }
    val trick = (json \ "trick").get match {
      case JsString("None") => None
      case trickJson => Some(TrickJsonFormatter.parseTrickJson(trickJson.asInstanceOf[JsObject], playerUtil, CardManagerJsonFormatter.parseCardManagerJson((json \ "cardManager").get.asInstanceOf[JsObject])))
      case default => None
    }
    val currentIndex = (json \ "currentIndex").get match {
      case JsString("None") => None
      case JsNumber(index) => Some(index.toInt)
      case default => None
    }
    MatchSnapshot(matchImpl, round, trick, currentIndex, gameState, methodEntryPoint)
  }

  private object PlayerJsonFormatter {
    def createPlayerJson(abstractPlayer: AbstractPlayer): JsValue = {
      Json.obj(
        "id" -> abstractPlayer.id.toString,
        "name" -> abstractPlayer.name,
        "hand" -> createHandJson(abstractPlayer.hand),
        "doglife" -> abstractPlayer.doglife,
        "playerType" -> PlayerFactory.parsePlayerType(abstractPlayer).toString
      )
    }

    private def createHandJson(hand: Option[Hand]): JsValue = {
      if (hand.isDefined) {
        Json.obj(
          "cards" -> JsArray(
            hand.get.cards.map(card => CardJsonFormatter.createCardJson(card))
          )
        )
      } else {
        JsString("None")
      }
    }

    def parsePlayerJson(playerJson: JsObject, playerUtil: PlayerUtil, cardManager: CardManager): AbstractPlayer = {
      val id = UUID.fromString((playerJson \ "id").get.asInstanceOf[JsString].value)
      val name = (playerJson \ "name").get.asInstanceOf[JsString].value
      val handJson = playerJson \ "hand"
      val doglife = (playerJson \ "doglife").get.asInstanceOf[JsBoolean].value
      val playerType = Playertype.valueOf((playerJson \ "playerType").get.asInstanceOf[JsString].value)

      val hand = if (handJson.isInstanceOf[JsString]) {
        None
      } else {
        Some(parseHandJson(handJson.get.asInstanceOf[JsObject], cardManager))
      }

      playerUtil.handlePlayer(id, name, hand, doglife, playerType)
    }

    private def parseHandJson(handJson: JsObject, cardManager: CardManager): Hand = {
      val cards = (handJson \ "cards").get.asInstanceOf[JsArray].value.map(cardJson => {
        val card = CardJsonFormatter.parseCardJson(cardJson.asInstanceOf[JsObject])
        cardManager.grabSpecificCard(card)
      })
      Hand(cards.toList)
    }

  }

  private object TrickJsonFormatter {
    def createTrickJson(trick: Trick): JsValue = {
      Json.obj(
        "plays" -> JsArray(
          trick.cards.map { case (card, player) => Json.obj(
            "card" -> CardJsonFormatter.createCardJson(card),
            "player" -> PlayerJsonFormatter.createPlayerJson(player)
          )}.toList
        ),
        "firstCard" -> (if (trick.firstCard.isDefined) CardJsonFormatter.createCardJson(trick.firstCard.get) else JsString("")),
        "winner" -> (if (trick.winner != null) PlayerJsonFormatter.createPlayerJson(trick.winner) else JsString("")),
        "finished" -> JsBoolean(trick.finished)
      )
    }

    def parseTrickJson(trickJson: JsObject, playerUtil: PlayerUtil, cardManager: CardManager): Trick = {
      val plays = (trickJson \ "plays").get
      val playsList = plays.asInstanceOf[JsArray].value.map(playJson => {
        val card = CardJsonFormatter.parseCardJson((playJson \ "card").get.asInstanceOf[JsObject])
        val player = PlayerJsonFormatter.parsePlayerJson((playJson \ "player").get.asInstanceOf[JsObject], playerUtil, cardManager)
        (card, player)
      }).groupBy(_._1).map((card, list) => (card, list.map(_._2).head))
      val firstCard = if ((trickJson \ "firstCard").get.isInstanceOf[JsString]) {
        None
      } else {
        Some(CardJsonFormatter.parseCardJson((trickJson \ "firstCard").get.asInstanceOf[JsObject]))
      }
      val winner = if ((trickJson \ "winner").get.isInstanceOf[JsString]) {
        null
      } else {
        PlayerJsonFormatter.parsePlayerJson((trickJson \ "winner").get.asInstanceOf[JsObject], playerUtil, cardManager)
      }
      val finished = (trickJson \ "finished").get.asInstanceOf[JsBoolean].value
      val hashed = HashMap(playsList.toSeq*)
      Trick(hashed, winner, finished, firstCard)
    }
  }

  private object RoundJsonFormatter {

    def createRoundJson(round: Round): JsValue = {
      Json.obj(
        "tricks" -> JsArray(
          round.tricklist.map(trick => TrickJsonFormatter.createTrickJson(trick))
        ),
        "playersin" -> JsArray(
          round.playersin.map(player => PlayerJsonFormatter.createPlayerJson(player))
        ),
        round.playersout match {
          case null => "playersout" -> JsString("None")
          case default =>
            "playersout" -> JsArray(
              round.playersout.map(player => PlayerJsonFormatter.createPlayerJson(player))
            )
        },
        "queue" -> Json.obj(
          "currentIndx" -> round.playerQueue.currentIndex,
          "players" -> JsArray(
            round.playerQueue.convertToArray().map(player => PlayerJsonFormatter.createPlayerJson(player))
          )
        ),
        "trumpSuit" -> JsNumber(round.trumpSuit.ordinal),
        "startingPlayer" -> JsNumber(round.playerQueue.currentIndex),
        "winner" -> (if (round.winner != null) PlayerJsonFormatter.createPlayerJson(round.winner) else JsString("None")),
        "firstRound" -> JsBoolean(round.firstRound)
      )
    }

    def parseRoundJson(roundJson: JsObject, playerUtil: PlayerUtil, cardManager: CardManager): Round = {
      val tricks = (roundJson \ "tricks").get
      val tricksList = tricks.asInstanceOf[JsArray].value.map(trickJson => TrickJsonFormatter.parseTrickJson(trickJson.asInstanceOf[JsObject], playerUtil, cardManager)).toList
      val playersin = (roundJson \ "playersin").get.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cardManager)).toList
      val playersout = (roundJson \ "playersout").get match {
        case JsString("None") => List()
        case default => (roundJson \ "playersout").get.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cardManager)).toList
      }
      val trumpSuit = Suit.fromOrdinal((roundJson \ "trumpSuit").get.asInstanceOf[JsNumber].value.toInt)
      val startingPlayer = (roundJson \ "startingPlayer").get.asInstanceOf[JsNumber].value.toInt
      val winner = if ((roundJson \ "winner").get.isInstanceOf[JsString]) {
        null
      } else {
        PlayerJsonFormatter.parsePlayerJson((roundJson \ "winner").get.asInstanceOf[JsObject], playerUtil, cardManager)
      }
      val firstRound = (roundJson \ "firstRound").get.asInstanceOf[JsBoolean].value
      Round(trumpSuit = trumpSuit, tricklist = tricksList, playersin = playersin, playersout = playersout, startingPlayer = startingPlayer, winner = winner, firstRound = firstRound)
    }
  }

  private object MatchJsonFormatter {

    def createMatchJson(matchImpl: Match): JsValue = {
      Json.obj(
        "totalplayers" -> JsArray(
          matchImpl.totalplayers.map(player => PlayerJsonFormatter.createPlayerJson(player))
        ),
        "numberofcards" -> JsNumber(matchImpl.numberofcards),
        "dogLife" -> JsBoolean(matchImpl.dogLife),
        "roundlist" -> JsArray(
          matchImpl.roundlist.map(round => RoundJsonFormatter.createRoundJson(round))
        )
      )
    }

    def parseMatchJson(matchJson: JsObject, playerUtil: PlayerUtil, cardManager: CardManager): Match = {
      val totalplayers = (matchJson \ "totalplayers").get.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cardManager)).toList
      val numberofcards = (matchJson \ "numberofcards").get.asInstanceOf[JsNumber].value.toInt
      val dogLife = (matchJson \ "dogLife").get.asInstanceOf[JsBoolean].value
      val roundlist = (matchJson \ "roundlist").get.asInstanceOf[JsArray].value.map(roundJson => RoundJsonFormatter.parseRoundJson(roundJson.asInstanceOf[JsObject], playerUtil, cardManager)).toList
      Match(totalplayers, numberofcards, dogLife, roundlist, cardManager)
    }

  }

  private object CardJsonFormatter {

    def createCardJson(card: Card): JsObject = {
      Json.obj(
        "value" -> JsNumber(card.cardValue.ordinal),
        "suit" -> JsNumber(card.suit.ordinal)
      )
    }

    def parseCardJson(cardJson: JsObject): Card = {
      val value = (cardJson \ "value").get.asInstanceOf[JsNumber].value.toInt
      val suit = (cardJson \ "suit").get.asInstanceOf[JsNumber].value.toInt
      Card(CardValue.fromOrdinal(value), Suit.fromOrdinal(suit))
    }

  }

  private object CardManagerJsonFormatter {
    def createCardManagerJson(cardManager: CardManager): JsObject = {
      Json.obj(
        "cardContainer" -> Json.obj(
          "card" -> JsArray(
            cardManager.cardContainer.map(card => CardJsonFormatter.createCardJson(card))
          )
        ),
        "currentIdx" -> JsNumber(cardManager.currentIndx)
      )
    }

    def parseCardManagerJson(cardManagerJson: JsObject): CardManager = {
      val cardContainer = (cardManagerJson \ "cardContainer").get.asInstanceOf[JsObject]
      val cards = (cardContainer \ "card").get.asInstanceOf[JsArray].value.map(cardJson => CardJsonFormatter.parseCardJson(cardJson.asInstanceOf[JsObject])).toList
      val currentIdx = (cardManagerJson \ "currentIdx").get.asInstanceOf[JsNumber].value.toInt
      val cardManager = KnockOutWhist.config.cardManager
      cardManager.setState(cards, currentIdx)
      cardManager
    }

  }

}
