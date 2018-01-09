package org.michal.repository

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID

import akka.actor.ActorSystem
import io.getquill.MappedEncoding
//import org.apache.cassandra.utils.UUIDGen

import scala.concurrent.ExecutionContextExecutor

class UserRepository(implicit actorSystem: ActorSystem) {

  implicit val ec: ExecutionContextExecutor = actorSystem.dispatcher

//  lazy val ttl = config.cassandraConfig.getInt("ttl")

//  implicit val encodeLocalDateTime: MappedEncoding[LocalDateTime, UUID] =
//    MappedEncoding[LocalDateTime, UUID](x => UUIDGen.getTimeUUID(x.toInstant(ZoneOffset.UTC).toEpochMilli))

}
