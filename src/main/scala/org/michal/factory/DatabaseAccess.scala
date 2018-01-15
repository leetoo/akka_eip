package org.michal.factory

import com.typesafe.config.ConfigFactory



object Context {
  val config = ConfigFactory.load()
  val url = config.getString("cassandra.url")
//  val sparkConf: SparkConf = new SparkConf().setAppName("Spark-cassandra-akka-rest-example").setMaster("local[4]")
//    .set("spark.cassandra.connection.host", url)
//  val spark = SparkSession.builder().config(sparkConf).getOrCreate()
//  val sc: SparkContext = spark.sparkContext
//  val keyspace = config.getString("cassandra.keyspace")
//  val tableName = config.getString("cassandra.tableName")
}