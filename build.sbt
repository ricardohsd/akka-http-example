name := "AkkaHttpExample"
version := "1.0"
scalaVersion := "2.12.1"

resolvers ++= {
  Seq(
    "Confluent" at "http://packages.confluent.io/maven/",
    "github/softprops maven" at "http://dl.bintray.com/content/softprops/maven"
  )
}

libraryDependencies ++= {
  Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.13.0",
  "io.circe" %% "circe-generic" % "0.7.0",
  "io.circe" %% "circe-java8" % "0.7.0"
  )
}