package org.michal.factory

import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import com.datastax.spark.connector._
import org.michal.domain.User

import scala.util.Try

trait DatabaseAccess {

  import Context._

  def create(user: User): Boolean =
    Try(sc.parallelize(Seq(user)).saveToCassandra(keyspace, tableName)).toOption.isDefined

  def retrieve(id: String): Option[Array[User]] = Try(sc.cassandraTable[User](keyspace, tableName).where(s"id='$id'").collect()).toOption
}

object DatabaseAccess extends DatabaseAccess


object Context {
  val config = ConfigFactory.load()
  val url = config.getString("cassandra.url")
  val sparkConf: SparkConf = new SparkConf().setAppName("Saprk-cassandra-akka-rest-example").setMaster("local[4]")
    .set("spark.cassandra.connection.host", url)
  val spark = SparkSession.builder().config(sparkConf).getOrCreate()
  val sc = spark.sparkContext
  val keyspace = config.getString("cassandra.keyspace")
  val tableName = config.getString("cassandra.tableName")
}