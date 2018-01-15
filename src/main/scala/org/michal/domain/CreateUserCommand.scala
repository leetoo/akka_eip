package org.michal.domain

import com.michal.domain.proto.schema.{CreateUserCommandProto, CreateUserEventProto}
import org.michal.{Command, Event, Msg}
import shapeless.TypeCase

case class CreateUserCommand(user: User) extends Command(CreateUserCommand.msgType){
  override def toProto = CreateUserCommandProto(msgType, Option(user.toProto))
}

object CreateUserCommand {
  val msgType = "CreateUserCommand"

  def matcher: TypeCase[Msg[CreateUserCommand]] = TypeCase[Msg[CreateUserCommand]]

  def apply(bytes: Array[Byte]): CreateUserCommand = {
    val pr = CreateUserCommandProto.parseFrom(bytes)
    val opt = pr.user.get
    CreateUserCommand(User(opt.id, opt.name, opt.email))
  }
}

case class CreateUserEvent(user: User) extends Event(CreateUserEvent.msgType) {
  override def toProto = CreateUserEventProto(msgType, Option(user.toProto))
}

object CreateUserEvent {
  val msgType = "CreateUserEvent"

  def matcher: TypeCase[Msg[CreateUserEvent]] = TypeCase[Msg[CreateUserEvent]]

  def apply(bytes: Array[Byte]): CreateUserEvent = {
    val pr = CreateUserEventProto.parseFrom(bytes)
    val opt = pr.user.get
    CreateUserEvent(User(opt.id, opt.name, opt.email))
  }
}
