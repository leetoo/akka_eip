package org.michal.domain

import akka.actor.ActorRef
import akka.cluster.sharding.ShardRegion.EntityId
import org.michal.{Msg, Response}
import shapeless.TypeCase

case class CCResponse(ccid: String, ccType: String, ccMsgNo: Int, origId: String, entityId: EntityId, originator: ActorRef)
  extends Response(CCResponse.cCResponse) {
  override def toProto = ???
}

object CCResponse {
    val matcher: TypeCase[Msg[CCResponse]] = TypeCase[Msg[CCResponse]]
    val cCResponse = "CCResponse"
}