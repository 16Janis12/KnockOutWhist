package de.knockoutwhist.persistence.formats

import de.knockoutwhist.KnockOutWhist
import de.knockoutwhist.cards.*
import de.knockoutwhist.control.controllerBaseImpl.BaseGameLogicSnapshot
import de.knockoutwhist.control.controllerBaseImpl.sublogic.{BasePlayerTieLogic, BasePlayerTieLogicSnapshot}
import de.knockoutwhist.control.{GameLogic, GameState, LogicSnapshot}
import de.knockoutwhist.persistence.{MatchSnapshot, MethodEntryPoint}
import de.knockoutwhist.player.{AbstractPlayer, PlayerData, PlayerFactory, Playertype}
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
      matchSnapshot.entryPoint match {
        case None => "entryPoint" -> JsString("None")
        case Some(entryPoint) => "entryPoint" -> JsString(entryPoint.toString)
      },
      matchSnapshot.gameLogicSnapShot match {
        case None => "gameLogic" -> JsString("None")
        case Some(gameLogic) => "gameLogic" -> BaseGameLogicJsonFormatter.createBaseGameLogicJson(gameLogic.asInstanceOf[BaseGameLogicSnapshot])
      },
    )
    js.toString().getBytes
  }

  override def parseFormat(bytes: Array[Byte]): MatchSnapshot = {
    val json = Json.parse(new String(bytes))
    val methodEntryPoint = (json \ "entryPoint").get match {
      case JsString("None") => None
      case JsString(methodEntryPoint) => Some(MethodEntryPoint.valueOf(methodEntryPoint))
      case default => None
    }
    val gameLogic = (json \ "gameLogic").get match {
      case JsString("None") => None
      case gameLogicJson: JsObject => Some(BaseGameLogicJsonFormatter.createBaseGameLogicFromJson(gameLogicJson, playerUtil))
      case default => None
    }

    MatchSnapshot(methodEntryPoint, gameLogic.asInstanceOf[Option[LogicSnapshot[GameLogic]]])
  }

  private object BaseGameLogicJsonFormatter {

    def createBaseGameLogicJson(baseGameLogic: BaseGameLogicSnapshot): JsValue = {
      Json.obj(
        "gameState" -> JsString(baseGameLogic.savedState.toString),

        "cardManagerContainer" -> Json.obj(
          "cardContainer" -> Json.obj(
            "card" ->
              (if (baseGameLogic.cardContainer.isDefined) JsArray(
                baseGameLogic.cardContainer.get
                  .map(card => CardJsonFormatter.createCardJson(card)))
              else JsString("None"))
          ),
          "currentIdx" ->
            (if (baseGameLogic.cardIndex.isDefined) JsNumber(baseGameLogic.cardIndex.get) else JsString("None"))
        ),

        "match" -> (if (baseGameLogic.currentMatch.isDefined) MatchJsonFormatter.createMatchJson(baseGameLogic.currentMatch.get) else JsString("None")),
        "round" -> (if (baseGameLogic.currentRound.isDefined) RoundJsonFormatter.createRoundJson(baseGameLogic.currentRound.get) else JsString("None")),
        "trick" -> (if (baseGameLogic.currentTrick.isDefined) TrickJsonFormatter.createTrickJson(baseGameLogic.currentTrick.get) else JsString("None")),
        "player" -> (if (baseGameLogic.currentPlayer.isDefined) PlayerJsonFormatter.createPlayerJson(baseGameLogic.currentPlayer.get) else JsString("None")),

        "queue" -> Json.obj(
          "currentIndx" -> (if (baseGameLogic.playerIndex.isDefined) JsNumber(baseGameLogic.playerIndex.get) else JsString("None")),
          "players" ->
            (if (baseGameLogic.players.isDefined) JsArray(
              baseGameLogic.players.get
                .map(player => PlayerJsonFormatter.createPlayerJson(player)))
            else JsString("None"))
        ),
      )
    }

    def createBaseGameLogicFromJson(baseGameLogicJson: JsObject, playerUtil: PlayerUtil): BaseGameLogicSnapshot = {
      val gameState = GameState.valueOf((baseGameLogicJson \ "gameState").get.asInstanceOf[JsString].value)

      val cardContainerJson = (baseGameLogicJson \ "cardManagerContainer" \ "cardContainer" \ "card").get
      val cardContainer = if (cardContainerJson.isInstanceOf[JsString]) {
        None
      } else {
        Some(cardContainerJson.asInstanceOf[JsArray].value.map(cardJson => CardJsonFormatter.parseCardJson(cardJson.asInstanceOf[JsObject])).toList)
      }
      val cc = cardContainer match {
        case Some(cards) => cards
        case None => List.empty[Card]
      }
      
      val cardIndex = (baseGameLogicJson \ "cardManagerContainer" \ "currentIdx").get match {
        case JsString("None") => None
        case JsNumber(idx) => Some(idx.toInt)
        case _ => None
      }

      val matchImpl = (baseGameLogicJson \ "match").get match {
        case JsString("None") => None
        case matchJson: JsObject => Some(MatchJsonFormatter.parseMatchJson(matchJson, playerUtil, cc))
        case _ => None
      }

      val round = (baseGameLogicJson \ "round").get match {
        case JsString("None") => None
        case roundJson: JsObject => Some(RoundJsonFormatter.parseRoundJson(roundJson, playerUtil, cc))
        case _ => None
      }

      val trick = (baseGameLogicJson \ "trick").get match {
        case JsString("None") => None
        case trickJson: JsObject => Some(TrickJsonFormatter.parseTrickJson(trickJson, playerUtil, cc))
        case _ => None
      }

      val player = (baseGameLogicJson \ "player").get match {
        case JsString("None") => None
        case playerJson: JsObject => Some(PlayerJsonFormatter.parsePlayerJson(playerJson, playerUtil, cc))
        case _ => None
      }

      val queueJson = (baseGameLogicJson \ "queue").get
      val playerIndex = (queueJson \ "currentIndx").get match {
        case JsString("None") => None
        case JsNumber(idx) => Some(idx.toInt)
        case _ => None
      }
      val playersJson = (queueJson \ "players").get
      val players = if (playersJson.isInstanceOf[JsString]) {
        None
      } else {
        Some(playersJson.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cc)).toList)
      }
      BaseGameLogicSnapshot(
        gameState,
        cardContainer,
        cardIndex,
        matchImpl,
        round,
        trick,
        player,
        playerIndex,
        players,
        matchImpl match {
          case Some(m) => m.totalplayers.map(p => (p.id, p.generatePlayerData())).toMap
          case None => Map.empty[UUID, PlayerData]
        }
      )
    }
  }

  private object PlayerJsonFormatter {
    def createPlayerJson(abstractPlayer: AbstractPlayer): JsValue = {
      Json.obj(
        "id" -> abstractPlayer.id.toString,
        "name" -> abstractPlayer.name,
        "hand" -> createHandJson(abstractPlayer.currentHand()),
        "doglife" -> abstractPlayer.isInDogLife,
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

    def parsePlayerJson(playerJson: JsObject, playerUtil: PlayerUtil, cards: List[Card]): AbstractPlayer = {
      val id = UUID.fromString((playerJson \ "id").get.asInstanceOf[JsString].value)
      val name = (playerJson \ "name").get.asInstanceOf[JsString].value
      val handJson = playerJson \ "hand"
      val doglife = (playerJson \ "doglife").get.asInstanceOf[JsBoolean].value
      val playerType = Playertype.valueOf((playerJson \ "playerType").get.asInstanceOf[JsString].value)

      val hand = if (handJson.isEmpty || !handJson.get.isInstanceOf[JsObject]) {
        None
      } else {
        Some(parseHandJson(handJson.get.asInstanceOf[JsObject], cards))
      }

      playerUtil.handlePlayer(id, name, hand, doglife, playerType)
    }

    private def parseHandJson(handJson: JsObject, cc: List[Card]): Hand = {
      val cards = (handJson \ "cards").get.asInstanceOf[JsArray].value.map(cardJson => {
        val card = CardJsonFormatter.parseCardJson(cardJson.asInstanceOf[JsObject])
        CardFormatUtil.grabSpecificCard(card, cc)
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
          )
          }.toList
        ),
        "firstCard" -> (if (trick.firstCard.isDefined) CardJsonFormatter.createCardJson(trick.firstCard.get) else JsString("")),
        "winner" -> (if (trick.winner.isDefined) PlayerJsonFormatter.createPlayerJson(trick.winner.get) else JsString("")),
      )
    }

    def parseTrickJson(trickJson: JsObject, playerUtil: PlayerUtil, cc: List[Card]): Trick = {
      val plays = (trickJson \ "plays").get
      val playsList = plays.asInstanceOf[JsArray].value.map(playJson => {
        val card = CardJsonFormatter.parseCardJson((playJson \ "card").get.asInstanceOf[JsObject])
        val player = PlayerJsonFormatter.parsePlayerJson((playJson \ "player").get.asInstanceOf[JsObject], playerUtil, cc)
        (card, player)
      }).groupBy(_._1).map((card, list) => (card, list.map(_._2).head))
      val firstCard = if ((trickJson \ "firstCard").get.isInstanceOf[JsString]) {
        None
      } else {
        Some(CardJsonFormatter.parseCardJson((trickJson \ "firstCard").get.asInstanceOf[JsObject]))
      }
      val winner = if ((trickJson \ "winner").get.isInstanceOf[JsString]) {
        None
      } else {
        Some(PlayerJsonFormatter.parsePlayerJson((trickJson \ "winner").get.asInstanceOf[JsObject], playerUtil, cc))
      }
      val hashed = HashMap(playsList.toSeq *)
      Trick(hashed, winner, firstCard)
    }
  }

  private object RoundJsonFormatter {

    def createRoundJson(round: Round): JsValue = {
      Json.obj(
        "tricks" -> JsArray(
          round.tricklist.map(trick => TrickJsonFormatter.createTrickJson(trick))
        ),
        "trumpSuit" -> JsNumber(round.trumpSuit.ordinal),
        "winner" -> (if (round.winner.isDefined) PlayerJsonFormatter.createPlayerJson(round.winner.get) else JsString("None")),
        "firstRound" -> JsBoolean(round.firstRound)
      )
    }

    def parseRoundJson(roundJson: JsObject, playerUtil: PlayerUtil, cc: List[Card]): Round = {
      val tricks = (roundJson \ "tricks").get
      val tricksList = tricks.asInstanceOf[JsArray].value.map(trickJson => TrickJsonFormatter.parseTrickJson(trickJson.asInstanceOf[JsObject], playerUtil, cc)).toList
      val trumpSuit = Suit.fromOrdinal((roundJson \ "trumpSuit").get.asInstanceOf[JsNumber].value.toInt)
      val winner = if ((roundJson \ "winner").get.isInstanceOf[JsString]) {
        None
      } else {
        Some(PlayerJsonFormatter.parsePlayerJson((roundJson \ "winner").get.asInstanceOf[JsObject], playerUtil, cc))
      }
      val firstRound = (roundJson \ "firstRound").get.asInstanceOf[JsBoolean].value
      Round(trumpSuit = trumpSuit, tricklist = tricksList, winner = winner, firstRound = firstRound)
    }
  }

  private object MatchJsonFormatter {

    def createMatchJson(matchImpl: Match): JsValue = {
      Json.obj(
        "totalplayers" -> JsArray(
          matchImpl.totalplayers.map(player => PlayerJsonFormatter.createPlayerJson(player))
        ),
        "playerIn" -> JsArray(
          matchImpl.playersIn.map(player => PlayerJsonFormatter.createPlayerJson(player))
        ),
        "numberofcards" -> JsNumber(matchImpl.numberofcards),
        "dogLife" -> JsBoolean(matchImpl.dogLife),
        "roundlist" -> JsArray(
          matchImpl.roundlist.map(round => RoundJsonFormatter.createRoundJson(round))
        )
      )
    }

    def parseMatchJson(matchJson: JsObject, playerUtil: PlayerUtil, cc: List[Card]): Match = {
      val totalplayers = (matchJson \ "totalplayers").get.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cc)).toList
      val playersIn = (matchJson \ "playerIn").get.asInstanceOf[JsArray].value.map(playerJson => PlayerJsonFormatter.parsePlayerJson(playerJson.asInstanceOf[JsObject], playerUtil, cc)).toList
      val numberofcards = (matchJson \ "numberofcards").get.asInstanceOf[JsNumber].value.toInt
      val dogLife = (matchJson \ "dogLife").get.asInstanceOf[JsBoolean].value
      val roundlist = (matchJson \ "roundlist").get.asInstanceOf[JsArray].value.map(roundJson => RoundJsonFormatter.parseRoundJson(roundJson.asInstanceOf[JsObject], playerUtil, cc)).toList
      Match(totalplayers, playersIn, numberofcards, dogLife, roundlist)
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

}
