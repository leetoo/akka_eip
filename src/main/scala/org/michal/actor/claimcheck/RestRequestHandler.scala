package org.michal.actor.claimcheck

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import org.michal.UserEntityActor
import org.michal.domain.{CreateUserCommand, GetUserRequest}

import scala.concurrent.duration._
import scala.language.postfixOps

class RestRequestHandler(cl: ActorRef) extends Actor {
  import akka.pattern.pipe
  import akka.pattern.ask
  import context._

  implicit val tout: Timeout = 7 seconds

  override def receive: Receive = {
    case r: GetUserRequest => (cl ? r) pipeTo sender
    case CreateUserCommand.matcher(createUser) => (cl ? createUser) pipeTo sender
  }
}

object RestRequestHandler {
  def props(cl: ActorRef): Props = Props(new RestRequestHandler(cl))
}
