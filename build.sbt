import scalariform.formatter.preferences._

mainClass in assembly := Some("gittertwitterbot.GitterTwitterBot")

scalariformPreferences := scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(DanglingCloseParenthesis, Preserve)

name := """gitter-twitter-bot"""

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.json4s" %% "json4s-native" % "3.6.6",
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

doctestTestFramework := DoctestTestFramework.ScalaTest
