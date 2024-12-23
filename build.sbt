


Compile/mainClass := Some("de.knockoutwhist.KnockOutWhist")

name := "KnockOutWhist"
version := {
  val major = sys.env.getOrElse("MAJOR_VERSION", "0")
  val minor = sys.env.getOrElse("MINOR_VERSION", "0")
  val buildNR = sys.env.getOrElse("BUI_COUNTER", "1")
  s"$major.$minor.$buildNR"
}

ThisBuild / organization := "de.knockoutwhist"
ThisBuild / version := version.value
ThisBuild / scalaVersion := "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "Projekt-zu-SE",
    assembly / mainClass := Some("de.knockoutwhist.KnockOutWhist"),
    assembly / assemblyJarName := s"KnockOutWhist-${version.value}.jar",
  )



libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"

Test / testOptions += Tests.Filter(_.equals("de.knockoutwhist.TestSequence"))

coverageEnabled := true
coverageFailOnMinimum := true
coverageMinimumStmtTotal := 85
coverageMinimumBranchTotal := 100
