package org.michal.boot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.apache.spark.SparkContext
import org.michal.services.{DataAccessService, RestService}
import org.michal.factory.Context


case class StartHttp(override val sc: SparkContext, override val keyspace: String, override val tableName: String)(implicit val system: ActorSystem,
                                                                                                                   implicit val materializer: ActorMaterializer) extends RestService {
  def startServer(address: String, port: Int) = {
    Http().bindAndHandle(sparkRoutes, address, port)
  }
}



object StartApplication extends App {
  StartApp
}

object StartApp {
  implicit val system: ActorSystem = ActorSystem("Spark-Service")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val sc: SparkContext = Context.sc

  val server = StartHttp(sc, Context.keyspace, Context.tableName)
  val config = Context.config
  val serverUrl = config.getString("http.interface")
  val port = config.getInt("http.port")
  server.startServer(serverUrl, port)
  val dao: DataAccessService = DataAccessService(sc, Context.keyspace, "cctable")
}