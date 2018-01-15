package org.michal

import java.net.URLDecoder

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import org.michal.actor.claimcheck.{CCReq, ClaimCheck}
import org.michal.domain._
import org.michal.services.SerUtil

import scala.io.Codec


class UserEntityActor extends PersistentActor with ActorLogging with ClaimCheck {

  var state: Option[User] = None

  override val ccSerializer = new SerUtil

  override def receiveCommand: Receive = {
    case Command.matcher(c) =>
      log.info(s"Handling command ${c.id}")
      handleCommand(state, c) match {
        case Event.matcher(ev) =>
          log.info(s"Handling event ${ev.id}")
          persist(ev) { persEv =>
            handleEvent(persEv)
            sender ! "success"
          }
        case Notification.matcher(n) =>
          log.info(n.toString)
          sender ! n.toString
      }
    case GetUserRequest.matcher(r) =>
      CCActor ! CCReq(
        ccid = None,
        messages = Some(Msg(GetUserResponse(state.get :: Nil), r) :: Nil),
        ccMsgNo = None,
        origId = r.id,
        entityId = r.payload.userId,
        originator = Some(sender)
      )
    case CCResponse.matcher(r) =>
      r.payload.originator ! r
  }


  override def receiveRecover: Receive = userRecover

  override def persistenceId: String = URLDecoder.decode(self.path.name, Codec.UTF8.name)

  def handleCommand(u: Option[User], c: Msg[Command]): Msg[_] = c.payload match {
    case cr: CreateUserCommand => state match {
      case None => Msg(CreateUserEvent(cr.user), c)
      case Some(_) => Msg(StringNotification("User already exists..."))
    }
  }

  def handleEvent(e: Msg[Event]): Unit = {
    state = e.payload match {
      case cr: CreateUserEvent => Some(cr.user)
      case _ => None
    }
  }

  def userRecover: Receive = {
    case Event.matcher(e) =>
      log.info(s"Recover applying message: $e")
      handleEvent(e)
  }
}

object UserEntityActor {
  def props(): Props = Props(new UserEntityActor)
}
