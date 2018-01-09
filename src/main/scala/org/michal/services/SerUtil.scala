package org.michal.services

import akka.serialization.SerializerWithStringManifest
import org.michal.{Msg, Payl, Request, Response}
import org.michal.domain.{CCResponse, GetUserRequest, GetUserResponse}
import org.michal.Response.{payloadInf => respPaylInf, protoInf => respProtoInf}
import org.michal.Request.{payloadInf => reqPaylInf, protoInf => reqProtoInf}

class SerUtil extends SerializerWithStringManifest {
  override def identifier = 345678

  override def manifest(o: AnyRef): String = o match {
    case response: Response =>
      response match {
        case pld: CCResponse => "CCResponse"
        case pld: GetUserResponse => GetUserResponse.msgType
      }

    case request: Request =>
      request match {
        case pld: GetUserRequest => GetUserRequest.msgType
      }
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case response: Response =>
      response match {
        case pld: GetUserResponse => pld.toProto.toByteArray
      }

    case request: Request =>
      request match {
        case pld: GetUserRequest => pld.toProto.toByteArray
      }
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): Msg[_ >: Request with Response <: Payl] = manifest match {
    case GetUserRequest.msgType => Msg(bytes, reqPaylInf)
    case GetUserResponse.msgType => Msg(bytes, respPaylInf)
  }
}
