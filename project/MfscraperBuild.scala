import sbt._
import sbt.Keys._

import sbtassembly.Plugin._
import AssemblyKeys._

object MfscraperBuild extends Build {

  lazy val mfscraper = Project(
    id = "mfscraper",
    base = file("."),
    settings = Project.defaultSettings ++ assemblySettings ++ Seq(
      name := "mfscraper",
      organization := "com.github.burn0ut07",
      version := "0.1.0-SNAPSHOT",
      scalaVersion := "2.9.2",
      libraryDependencies ++= Seq(
        "net.databinder.dispatch" %% "core" % "0.9.0",
        "org.clapper" %% "argot" % "0.4"
      ),
      test in assembly := {},
      jarName in assembly := "mfscraper.jar",
      mainClass in assembly := Some("com.github.burn0ut07.Main")
    )
  )
}
