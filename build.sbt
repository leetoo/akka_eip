
name := "akka_eip"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.8",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "com.typesafe.akka"          %% "akka-cluster"                        % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-metrics"                % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-sharding"               % "2.5.8",
  "com.typesafe.akka"          %% "akka-cluster-tools"                  % "2.5.8",
  "com.typesafe.akka" %% "akka-persistence" % "2.5.8",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.80-RC3",
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % "0.80-RC3" % "test",
//  "org.apache.cassandra" % "cassandra-all" % "3.9" exclude("ch.qos.logback", "logback-classic"),
//  "org.apache.spark" % "spark-core_2.11" % "2.2.1",
//  "org.apache.spark" % "spark-sql_2.11" % "2.2.1",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.0-RC1",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.0-RC1" % "test",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0-RC1",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
//  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.6",
  "io.getquill" %% "quill-cassandra" % "2.3.2",
//  "com.github.romix.akka"      %% "akka-kryo-serialization"             % "0.5.0",
  "org.cassandraunit" % "cassandra-unit"  % "3.3.0.2" % "test"
)
libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.8"
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query" % "2.5.8"
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % "2.5.8"

PB.pythonExe := "c:\\Users\\michal\\Anaconda3\\envs\\python27\\python.exe"
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)