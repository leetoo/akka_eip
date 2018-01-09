package org.michal.actor.claimcheck

import akka.actor.{Actor, ActorRef}
import akka.serialization.SerializerWithStringManifest
import com.typesafe.config.Config
import io.getquill._
import org.michal.persistence.CCService

trait ClaimCheck {
  this: Actor =>

  private[claimcheck] val ccconf: Config = context.system.settings.config.getConfig("cassandra-claim-check")
  val ccSerializer: SerializerWithStringManifest

  private lazy val cassCCCfg = new CassandraAsyncContext[SnakeCase](SnakeCase, ccconf)

  private[claimcheck] lazy val getCCService = new CCService(cassCCCfg, ccSerializer)

  def CCActor: ActorRef = context.system.actorOf(CCProcessor.props(getCCService))

}
