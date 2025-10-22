package de.knockoutwhist.persistence.formats

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json._
import de.knockoutwhist.persistence._
import de.knockoutwhist.control.GameState
import de.knockoutwhist.cards.{CardValue, Suit}

import java.util.UUID

class JSONFormatterBranchesSpec extends AnyWordSpec with Matchers {

  private def bytesOf(js: JsValue): Array[Byte] = js.toString().getBytes()

  "JSONFormatter.parseFormat" should {

    "treat unexpected JSON types as None for entryPoint and gameLogic" in {
      // Arrange
      val fmt = new JSONFormatter()
      val js = Json.obj(
        "entryPoint" -> JsNumber(1), // not a JsString("None") or valid name
        "gameLogic" -> JsNumber(2)   // not a JsString("None") or JsObject
      )

      // Act
      val parsed = fmt.parseFormat(bytesOf(js))

      // Assert
      parsed.entryPoint shouldBe None
      parsed.gameLogicSnapShot shouldBe None
    }

    "parse a BaseGameLogicSnapshot when all optional fields are encoded as None-like values" in {
      // Arrange
      val fmt = new JSONFormatter()
      val js = Json.obj(
        "entryPoint" -> JsString("None"),
        "gameLogic" -> Json.obj(
          "gameState" -> JsString(GameState.Lobby.toString),
          "cardManagerContainer" -> Json.obj(
            "cardContainer" -> Json.obj(
              "card" -> JsString("None")
            ),
            // invalid type (string but not "None") should be treated as None by parser
            "currentIdx" -> JsString("invalid")
          ),
          // explicit Nones for nested parts
          "match" -> JsString("None"),
          "round" -> JsString("None"),
          "trick" -> JsString("None"),
          "player" -> JsString("None"),
          "queue" -> Json.obj(
            "currentIndx" -> JsString("None"),
            "players" -> JsString("None")
          )
        )
      )

      // Act
      val parsed = fmt.parseFormat(bytesOf(js))

      // Assert
      parsed.entryPoint shouldBe None
      parsed.gameLogicSnapShot shouldBe defined
      val snap = parsed.gameLogicSnapShot.get.asInstanceOf[de.knockoutwhist.control.controllerBaseImpl.BaseGameLogicSnapshot]
      snap.savedState shouldBe GameState.Lobby
      snap.cardContainer shouldBe None
      snap.cardIndex shouldBe None
      snap.currentMatch shouldBe None
      snap.currentRound shouldBe None
      snap.currentTrick shouldBe None
      snap.currentPlayer shouldBe None
      snap.playerIndex shouldBe None
      snap.players shouldBe None
      snap.playerStates.isEmpty shouldBe true
    }

    "parse complex gameLogic with Some values across nested structures" in {
      // Arrange
      val fmt = new JSONFormatter()

      val id1 = UUID.randomUUID()
      val id2 = UUID.randomUUID()

      val cardObj = Json.obj(
        "value" -> JsNumber(CardValue.Ace.ordinal),
        "suit" -> JsNumber(Suit.Spades.ordinal)
      )

      val player1 = Json.obj(
        "id" -> JsString(id1.toString),
        "name" -> JsString("p1"),
        // hand None (so we don't depend on card container content here)
        "hand" -> JsString("None"),
        "doglife" -> JsBoolean(false),
        "playerType" -> JsString(de.knockoutwhist.player.Playertype.STUB.toString)
      )

      val player2 = player1 + ("id" -> JsString(id2.toString)) + ("name" -> JsString("p2"))

      val trick = Json.obj(
        "plays" -> JsArray(Seq()),
        // empty string should be treated as JsString hence None by parser
        "firstCard" -> JsString(""),
        "winner" -> JsString("")
      )

      val round = Json.obj(
        "tricks" -> JsArray(Seq(trick)),
        "trumpSuit" -> JsNumber(Suit.Hearts.ordinal),
        "winner" -> JsString("None"),
        "firstRound" -> JsBoolean(true)
      )

      val matchObj = Json.obj(
        "totalplayers" -> JsArray(Seq(player1, player2)),
        "playerIn" -> JsArray(Seq(player1, player2)),
        "numberofcards" -> JsNumber(5),
        "dogLife" -> JsBoolean(false),
        "roundlist" -> JsArray(Seq(round))
      )

      val queue = Json.obj(
        "currentIndx" -> JsNumber(1),
        "players" -> JsArray(Seq(player1, player2))
      )

      val gameLogic = Json.obj(
        "gameState" -> JsString(GameState.InGame.toString),
        "cardManagerContainer" -> Json.obj(
          "cardContainer" -> Json.obj(
            "card" -> JsArray(Seq(cardObj))
          ),
          "currentIdx" -> JsNumber(0)
        ),
        "match" -> matchObj,
        "round" -> round,
        "trick" -> Json.obj(
          // No plays, but specify firstCard explicitly as object to exercise object parsing branch
          "plays" -> JsArray(Seq()),
          "firstCard" -> cardObj,
          "winner" -> JsString("")
        ),
        "player" -> player1,
        "queue" -> queue
      )

      val js = Json.obj(
        "entryPoint" -> JsString(MethodEntryPoint.ControlMatch.toString),
        "gameLogic" -> gameLogic
      )

      // Act
      val parsed = fmt.parseFormat(bytesOf(js))

      // Assert
      parsed.entryPoint shouldBe Some(MethodEntryPoint.ControlMatch)
      parsed.gameLogicSnapShot shouldBe defined
      val snap = parsed.gameLogicSnapShot.get.asInstanceOf[de.knockoutwhist.control.controllerBaseImpl.BaseGameLogicSnapshot]

      snap.savedState shouldBe GameState.InGame
      snap.cardContainer.isDefined shouldBe true
      snap.cardContainer.get.size shouldBe 1
      snap.cardIndex shouldBe Some(0)

      snap.currentMatch.isDefined shouldBe true
      val m = snap.currentMatch.get
      m.totalplayers.map(_.name) should contain allElementsOf Seq("p1", "p2")
      m.roundlist.size shouldBe 1

      snap.currentRound.isDefined shouldBe true
      snap.currentTrick.isDefined shouldBe true
      snap.currentPlayer.isDefined shouldBe true

      snap.playerIndex shouldBe Some(1)
      snap.players.isDefined shouldBe true
      snap.players.get.map(_.name) should contain allElementsOf Seq("p1", "p2")
    }
  }
}
