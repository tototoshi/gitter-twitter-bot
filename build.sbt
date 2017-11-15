enablePlugins(AssemblyPlugin)

scalariformSettings

name := """gitter-twitter-bot"""

version := "1.0"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.scalaj" %% "scalaj-http" % "2.3.0",
  "org.json4s" %% "json4s-native" % "3.5.3",
  "org.twitter4j" % "twitter4j-core" % "4.0.6",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

doctestTestFramework := DoctestTestFramework.ScalaTest
