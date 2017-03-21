name := "akka2"

version := "1.0"

scalaVersion := "2.12.1"

val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" % "scalatest_2.12" % "3.2.0-SNAP4",
  "log4j" % "log4j" % "1.2.17"
)
    