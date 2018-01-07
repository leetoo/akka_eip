package org.michal.factory

import javax.ws.rs.ext.ParamConverter.Lazy

import akka.actor.ActorSystem
import akka.event.Logging
import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import com.datastax.spark.connector._
import org.michal.domain.{CCItem, User}

import scala.util.{Failure, Success, Try}

trait DatabaseAccess {

  implicit val system: ActorSystem
  val sc: SparkContext
  val keyspace: String
  val tableName: String

  def createCCItem(ccitem: CCItem): Boolean =
    Try(sc.parallelize(Seq(ccitem)).saveToCassandra(keyspace, tableName)).toOption.isDefined

  def retrieveCCItem(id: String): Option[Array[CCItem]] = Try(sc.cassandraTable[CCItem](keyspace, tableName).where(s"id='$id'").collect()).toOption

  def createUser(ccitem: User): Boolean =
    Try(sc.parallelize(Seq(ccitem)).saveToCassandra(keyspace, tableName)) match {
      case Success(a) => true
      case Failure(e) => system.log.error(e, "User persisting failed"); false
    }

  def retrieveUser(id: String): Option[Array[User]] = Try(sc.cassandraTable[User](keyspace, tableName).where(s"id='$id'").collect()).toOption
}

//object DatabaseAccess extends DatabaseAccess


object Context {
  val config = ConfigFactory.load()
  val url = config.getString("cassandra.url")
  val sparkConf: SparkConf = new SparkConf().setAppName("Spark-cassandra-akka-rest-example").setMaster("local[4]")
    .set("spark.cassandra.connection.host", url)
  val spark = SparkSession.builder().config(sparkConf).getOrCreate()
  val sc: SparkContext = spark.sparkContext
  val keyspace = config.getString("cassandra.keyspace")
  val tableName = config.getString("cassandra.tableName")
}