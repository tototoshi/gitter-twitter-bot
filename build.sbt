import AssemblyKeys._

assemblySettings

name := """gitter-twitter-bot"""

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "org.scalaj" %% "scalaj-http" % "0.3.16",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.twitter4j" % "twitter4j-core" % "4.0.2",
  "com.github.kxbmap" %% "configs" % "0.2.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)
