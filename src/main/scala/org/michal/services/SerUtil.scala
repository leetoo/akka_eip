package org.michal.services

import akka.serialization.SerializerWithStringManifest
import org.michal._
import org.michal.domain.{CCResponse, CreateUserEvent, GetUserRequest, GetUserResponse}
import org.michal.Response.{payloadInf => respPaylInf, protoInf => respProtoInf}
import org.michal.Request.{payloadInf => reqPaylInf, protoInf => reqProtoInf}
import org.michal.Event.{payloadInf => evPaylInf, protoInf => evProtoInf}

/**
  * Serializes/Deserializes protobuf Msg[_]
  */
class SerUtil extends SerializerWithStringManifest {
  override def identifier = 345678

  override def manifest(o: AnyRef): String = o match {
    case Event.matcher(event) =>
      event.payload match {
        case pld: CreateUserEvent => CreateUserEvent.msgType
      }
    case Response.matcher(response) =>
      response.payload match {
        case pld: GetUserResponse => GetUserResponse.msgType
        case pld: CCResponse => CCResponse.cCResponse
      }
    case Request.matcher(request) =>
      request.payload match {
        case pld: GetUserRequest => GetUserRequest.msgType
      }
  }

  override def toBinary(o: AnyRef): Array[Byte] = o match {
    case Response.matcher(response) =>
      response.toProto(respProtoInf).toByteArray
    case Request.matcher(request) =>
      request.toProto(reqProtoInf).toByteArray
    case Event.matcher(event) =>
      event.toProto(evProtoInf).toByteArray
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): Msg[_ >: Event with Request with Response <: Payl] = manifest match {
    case CreateUserEvent.msgType =>
      Msg(bytes, evPaylInf)
    case GetUserRequest.msgType =>
      Msg(bytes, reqPaylInf)
    case GetUserResponse.msgType =>
      Msg(bytes, respPaylInf)
  }
}
