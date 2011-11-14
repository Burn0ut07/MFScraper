seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)

libraryDependencies ++= Seq(
	"net.databinder" %% "dispatch-http" % "0.8.6",
	"commons-lang" % "commons-lang" % "2.6"
)

initialCommands := "import dispatch._"

mainClass := Some("Main")

scalaVersion := "2.9.1"