ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.5.1"


name := "KnockOutWhist"
version := {
  val major = sys.env.get("MAJOR_VERSION").getOrElse("0")
  val minor = sys.env.get("MINOR_VERSION").getOrElse("0")
  val buildNR = sys.env.get("BUI_COUNTER").getOrElse("1")
  s"$major.$minor.$buildNR"
}
organization := "de.knockoutwhist"

lazy val root = (project in file("."))
  .settings(
    name := "Projekt-zu-SE"
  )

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"

coverageEnabled := true