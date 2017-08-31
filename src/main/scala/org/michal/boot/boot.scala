package org.michal.boot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.apache.spark.SparkContext
import org.michal.actor.CCProcessor
import org.michal.services.{DataAccessService, RestService}
import org.michal.factory.Context


case class StartHttp(sc: SparkContext, override val dao: DataAccessService)(implicit val system: ActorSystem,
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

  val dao: DataAccessService = DataAccessService(sc, Context.keyspace, Context.tableName)
  val server = StartHttp(sc, dao)
  val config = Context.config
  val serverUrl = config.getString("http.interface")
  val port = config.getInt("http.port")
  server.startServer(serverUrl, port)
  system.actorOf(CCProcessor.props(dao))
}