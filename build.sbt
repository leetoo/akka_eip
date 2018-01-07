
name := "akka_eip"

version := "0.1"

scalaVersion := "2.11.11"

val akkaV = "2.5.5"
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.8",
  "com.typesafe.akka" % "akka-persistence_2.11" % "2.5.8",
  "com.typesafe.akka" % "akka-persistence-cassandra_2.11" % "0.80-RC3",
  "com.typesafe.akka" % "akka-persistence-cassandra-launcher_2.11" % "0.80-RC3" % "test",
  "org.apache.spark" % "spark-core_2.11" % "2.2.1",
  "org.apache.spark" % "spark-sql_2.11" % "2.2.1",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.11",
//  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % "test",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.6"
)