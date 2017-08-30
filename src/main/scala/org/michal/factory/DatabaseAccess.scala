package org.michal.factory

import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import com.datastax.spark.connector._
import org.michal.domain.{Identifiable, User}

import scala.util.Try
import scala.reflect.ClassTag

trait DatabaseAccess[A <: Identifiable] {

  val sc: SparkContext
  val keyspace: String
  val tableName: String


  def create[A: ClassTag](item: A): Boolean =
    Try(sc.parallelize(Seq(item)).saveToCassandra(keyspace, tableName)).toOption.isDefined

  def retrieve[A: ClassTag](id: String): Option[Array[A]] = Try(sc.cassandraTable[A](keyspace, tableName).where(s"ccid='$id'").collect()).toOption
}

//object DatabaseAccess extends DatabaseAccess


object Context {
  val config = ConfigFactory.load()
  val url = config.getString("cassandra.url")
  val sparkConf: SparkConf = new SparkConf().setAppName("Saprk-cassandra-akka-rest-example").setMaster("local[4]")
    .set("spark.cassandra.connection.host", url)
  lazy val sc: SparkContext = SparkSession.builder().config(sparkConf).getOrCreate().sparkContext
  val keyspace = config.getString("cassandra.keyspace")
  val tableName = config.getString("cassandra.tableName")
}