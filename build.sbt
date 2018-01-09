
name := "akka_eip"

version := "0.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % "2.5.8",
  "com.chuusai" % "shapeless_2.11" % "2.3.3",
  "com.typesafe.akka"          %% "akka-cluster"                        % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-metrics"                % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-sharding"               % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-tools"                  % "2.5.8",
  "com.typesafe.akka" % "akka-persistence_2.11" % "2.5.8",
  "com.typesafe.akka" % "akka-persistence-cassandra_2.11" % "0.80-RC3",
  "com.typesafe.akka" % "akka-persistence-cassandra-launcher_2.11" % "0.80-RC3" % "test",
  "org.apache.cassandra" % "cassandra-all" % "3.9" exclude("ch.qos.logback", "logback-classic"),
  "org.apache.spark" % "spark-core_2.11" % "2.2.1",
  "org.apache.spark" % "spark-sql_2.11" % "2.2.1",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % "test",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.6",
  "io.getquill" % "quill-cassandra_2.11" % "2.3.2",
  "org.cassandraunit" % "cassandra-unit"  % "3.3.0.2" % "test",
)
libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)