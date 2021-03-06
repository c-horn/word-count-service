name := "coding-sample"

scalaVersion := "2.11.8"

fork in run := true

connectInput in run := true

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.0"

libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % "10.0.0" % "test"
