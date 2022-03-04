name := "Graph DSL"

version := "0.1"

scalaVersion := "2.12.11"

lazy val akkaVersion = "2.5.19"
lazy val scalaTestVersion = "3.0.5"

libraryDependencies ++= Seq(

  "org.scalatest" %% "scalatest" % scalaTestVersion ,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion ,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion ,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion)

