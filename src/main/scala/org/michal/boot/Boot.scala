package org.michal.boot

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import io.getquill.{CassandraAsyncContext, SnakeCase}
import org.michal.{Command, UserEntityActor}
import org.michal.actor.claimcheck.CCProcessor
import org.michal.domain.{CreateUserCommand, GetUserRequest}
import org.michal.services.{RestService, SerUtil}
import org.michal.factory.Context
import org.michal.persistence.CCService

import scala.concurrent.Future


case class StartHttp(cluster: ActorRef)
                    (implicit val system: ActorSystem,
                     implicit val materializer: ActorMaterializer) extends RestService {
  def startServer(address: String, port: Int): Future[Http.ServerBinding] = {
    Http().bindAndHandle(sparkRoutes, address, port)
  }
}



object StartApplication extends App {
  StartApp
}

object StartApp {
  implicit val system: ActorSystem = ActorSystem("akka_eip")
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  //TODO: dodac cluster sharding


  private val cluster: ActorRef = ClusterSharding(system).start(
    typeName = "akka_eip_shard",
    entityProps = UserEntityActor.props(),
    settings = ClusterShardingSettings(system),
    extractEntityId(1),
    extractShardId(1)
  )

//  val sc: SparkContext = Context.sc
//  val server = StartHttp(sc, dao)
  val server = StartHttp(cluster)
  val config = Context.config
  val serverUrl = config.getString("http.interface")
  val port = config.getInt("http.port")



  server.startServer(serverUrl, port)

  private def extractEntityId(size: Long): ShardRegion.ExtractEntityId = {
    case req: GetUserRequest => (req.userId, req)
    case Command.matcher(cmd) => cmd.payload match {
      case cuc: CreateUserCommand => (cuc.user.id, cmd)
    }
  }

  private def extractShardId(size: Long): ShardRegion.ExtractShardId = {
    case req: GetUserRequest => (req.userId.hashCode % size).toString
    case Command.matcher(cmd) => cmd.payload match {
      case cuc: CreateUserCommand => (cuc.user.id.hashCode % size).toString
    }
  }

//  system.actorOf(CCProcessor.props(new CCService(new CassandraAsyncContext[SnakeCase](SnakeCase, ""), new SerUtil)))
}