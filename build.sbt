libraryDependencies ++= Seq(
  "net.databinder" %% "dispatch-http" % "0.8.6"
)

initialCommands := "import dispatch._"

mainClass := Some("Main")

scalaVersion := "2.9.1"