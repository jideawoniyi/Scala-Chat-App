name := "ScalaChatApp"

version := "0.1"

scalaVersion := "2.11.12" // Your installed Scala version

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.12", // Compatible with Scala 2.11
  "com.typesafe.akka" %% "akka-stream" % "2.5.32", // Compatible with Scala 2.11
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0" // A version compatible with Scala 2.11
  // Add other dependencies if needed
)
