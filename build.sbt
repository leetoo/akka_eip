
name := "akka_eip"

version := "0.1"

scalaVersion := "2.11.11"


val akkaV = "2.5.4"
libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.11" % "2.2.0",
  "org.apache.spark" % "spark-sql_2.11" % "2.2.0",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.9",
//  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.9" % "test",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.9",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.5"
)