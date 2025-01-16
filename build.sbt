


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
libraryDependencies +="io.github.mkpaz" % "atlantafx-base" % "2.0.1"
libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33"

libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0"

libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "21" classifier osName)
}

libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0"

Test / testOptions += Tests.Filter(_.equals("de.knockoutwhist.TestSequence"))

coverageEnabled := true
coverageFailOnMinimum := true
coverageMinimumStmtTotal := 85
coverageMinimumBranchTotal := 100
