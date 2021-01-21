ThisBuild / scalaVersion := "2.13.4"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / organization := "com.rresino"
ThisBuild / organizationName := "rresino"

ThisBuild / scalacOptions += "-target:jvm-1.8"
ThisBuild / javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

lazy val akkaVersion = "2.6.11"
lazy val scalaTestVersion = "3.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "akka4dummies",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
)
