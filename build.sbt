
name := "akka_eip"

version := "0.1"

scalaVersion := "2.12.4"

val akkaVersion = "2.5.11"
val akkaHttpVersion = "10.0.11"
val akkaPersistenceCassandraVersion = "0.84"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.chuusai" %% "shapeless" % "2.3.3",
  "com.typesafe.akka"          %% "akka-cluster"                        % akkaVersion,
  "com.typesafe.akka"          %% "akka-cluster-metrics"                % akkaVersion,
  "com.typesafe.akka"          %% "akka-cluster-sharding"               % akkaVersion,
  "com.typesafe.akka"          %% "akka-cluster-tools"                  % akkaVersion,

  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % akkaPersistenceCassandraVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % akkaPersistenceCassandraVersion % "test",

//  "org.apache.cassandra" % "cassandra-all" % "3.9" exclude("ch.qos.logback", "logback-classic"),
//  "org.apache.spark" % "spark-core_2.11" % "2.2.1",
//  "org.apache.spark" % "spark-sql_2.11" % "2.2.1",

  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,


  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
//  "com.datastax.spark" % "spark-cassandra-connector_2.11" % "2.0.6",
  "io.getquill" %% "quill-cassandra" % "2.3.2",
//  "com.github.romix.akka"      %% "akka-kryo-serialization"             % "0.5.0",
  "org.cassandraunit" % "cassandra-unit"  % "3.3.0.2" % "test"
)
libraryDependencies += "com.trueaccord.scalapb" %% "scalapb-runtime" % com.trueaccord.scalapb.compiler.Version.scalapbVersion % "protobuf"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-distributed-data" % akkaVersion

PB.pythonExe := "c:\\Users\\michal\\Anaconda3\\envs\\python27\\python.exe"
PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
) in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
) Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)