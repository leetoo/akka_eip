package org.michal.domain

import com.michal.domain.proto.schema.{GetUserRequestProto, GetUserResponseProto}
import org.michal.{Msg, Request, Response}
import shapeless.TypeCase

case class GetUserRequest(userId: String) extends Request(GetUserRequest.msgType) {
  override def toProto = GetUserRequestProto(msgType = msgType, userId = userId)
}
case class GetUserResponse(users: List[User]) extends Response(GetUserResponse.msgType) {
  override def toProto = GetUserResponseProto(msgType = msgType, users = users.map(_.toProto))
}

object GetUserResponse {
  val msgType = "GetUserResponse"

  val matcher: TypeCase[Msg[GetUserResponse]] = TypeCase[Msg[GetUserResponse]]

  def apply(bytes: Array[Byte]): GetUserResponse = {
    val pr = GetUserResponseProto.parseFrom(bytes)
    val users: Seq[User] = pr.users.map(up => User(up.id, up.name, up.email))
    GetUserResponse(users.toList)
  }
}

object GetUserRequest {
  val msgType = "GetUserRequest"

  val matcher: TypeCase[Msg[GetUserRequest]] = TypeCase[Msg[GetUserRequest]]

  def apply(bytes: Array[Byte]): GetUserRequest = {
    val pr = GetUserRequestProto.parseFrom(bytes)
    GetUserRequest(pr.userId)
  }
}
