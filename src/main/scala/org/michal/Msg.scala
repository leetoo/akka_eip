package org.michal

import com.michal.domain.proto.schema.MessageProto
import org.michal.domain._
import shapeless.TypeCase


case class Msg[P <: Payl](payload: P, id: String = "") {
  def toProto(adapter: P => PrBuf): MessageProto = MessageProto(
    id = id,
    msgType = payload.msgType
  )
}

object Msg {

  def apply[T <: Payl](proto: MessageProto, payloadInference: PartialFunction[MessageProto, T]): Msg[T] = Msg(
    id = proto.id,
    payload = payloadInference(proto)
  )

  def apply[T <: Payl](bytes: Array[Byte], payloadInference: PartialFunction[MessageProto, T]): Msg[T] = {
    val proto = MessageProto.parseFrom(bytes)
    Msg(proto, payloadInference)
  }

  def apply[T <: Payl](payload: T, source: Msg[_]): Msg[T] =
    Msg(payload, source.id)
}

abstract class Payl(val msgType: String)
abstract class Response(override val msgType: String) extends Payl(msgType) {
  def toProto: PrBuf

  def payloadInference: PartialFunction[MessageProto, _ <: Response] = {
    case m: MessageProto if m.msgType == GetUserResponse.msgType => GetUserResponse(m.toByteArray)
  }
}
abstract class Event(override val msgType: String) extends Payl(msgType) {
  def toProto: PrBuf
}
abstract class Command(override val msgType: String) extends Payl(msgType) {
  def toProto: PrBuf
}
abstract class Request(override val msgType: String) extends Payl(msgType) {
  def toProto: PrBuf
}
abstract class Notification(override val msgType: String) extends Payl(msgType) {
  def toProto: PrBuf
}

object Request {

  val matcher: TypeCase[Msg[Request]] = TypeCase[Msg[Request]]

  def protoInf[P <: Payl]: PartialFunction[P, PrBuf] = {
    case payload: GetUserRequest => payload.toProto
  }

  def payloadInf: PartialFunction[MessageProto, _ <: Request] = {
    case m: MessageProto if m.msgType == GetUserRequest.msgType => GetUserRequest(m.payload.toByteArray)
  }
}

object Response {

  val matcher: TypeCase[Msg[Request]] = TypeCase[Msg[Request]]

  def protoInf[P <: Payl]: PartialFunction[P, PrBuf] = {
    case payload: GetUserResponse => payload.toProto
  }

  def payloadInf: PartialFunction[MessageProto, _ <: Response] = {
    case m: MessageProto if m.msgType == GetUserResponse.msgType => GetUserResponse(m.payload.toByteArray)
  }
}

object Event {

  val matcher: TypeCase[Msg[Event]] = TypeCase[Msg[Event]]

  def protoInf[P <: Payl]: PartialFunction[P, PrBuf] = {
    case payload: CreateUserEvent => payload.toProto
  }

  def payloadInf: PartialFunction[MessageProto, _ <: Event] = {
    case m: MessageProto if m.msgType == CreateUserEvent.msgType => CreateUserEvent(m.payload.toByteArray)
  }
}

object Command {

  val matcher: TypeCase[Msg[Command]] = TypeCase[Msg[Command]]

  def protoInf[P <: Payl]: PartialFunction[P, PrBuf] = {
    case payload: CreateUserCommand => payload.toProto
  }

  def payloadInf: PartialFunction[MessageProto, _ <: Command] = {
    case m: MessageProto if m.msgType == CreateUserCommand.msgType => CreateUserCommand(m.payload.toByteArray)
  }

}

object Notification {
  val matcher: TypeCase[Msg[Notification]] = TypeCase[Msg[Notification]]
}