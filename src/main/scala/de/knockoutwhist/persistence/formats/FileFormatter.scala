package de.knockoutwhist.persistence.formats

import de.knockoutwhist.cards.Hand
import de.knockoutwhist.persistence.MatchSnapshot
import de.knockoutwhist.player.{AbstractPlayer, PlayerFactory, Playertype}

import java.util.UUID
import scala.collection.mutable

trait FileFormatter {

  def formatName: String
  def fileEnding: String

  def createFormat(matchSnapshot: MatchSnapshot): Array[Byte]
  def parseFormat(bytes: Array[Byte]): MatchSnapshot

  def writeToFile(matchSnapshot: MatchSnapshot, path: String): Unit = {
    val file = new java.io.File(path)
    val out = new java.io.FileOutputStream(file)
    out.write(createFormat(matchSnapshot))
    out.close()
  }

  def readFromFile(path: String): MatchSnapshot = {
    val file = new java.io.File(path)
    val in = new java.io.FileInputStream(file)
    val data = new Array[Byte](file.length.toInt)
    in.read(data)
    in.close()
    parseFormat(data)
  }

  class PlayerUtil {
    private val players: mutable.HashMap[UUID, AbstractPlayer] = mutable.HashMap()

    private def handlePlayer(id: UUID, name: String, hand: Option[Hand], dog_life: Boolean, player_type: Playertype): AbstractPlayer = {
      players.get(id) match {
        case Some(player) => player
        case None =>
          var player = PlayerFactory.createPlayer(name, player_type)
          if(hand.isDefined) player = player.provideHand(hand.get)
          if(dog_life) player = player.setDogLife()
          players.put(id, player)
          player
      }
    }
  }

}
