package de.knockoutwhist.persistence.formats

import de.knockoutwhist.persistence.MatchSnapshot

class JSONFormatter extends FileFormatter {

  private val playerUtil = new PlayerUtil

  override def formatName: String = "JSON"

  override def fileEnding: String = "json"

  override def createFormat(matchSnapshot: MatchSnapshot): Array[Byte] = ???

  override def parseFormat(bytes: Array[Byte]): MatchSnapshot = ???




}
