package org.michal

import java.net.URLDecoder

import akka.actor.{ActorLogging, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import org.michal.actor.claimcheck.{CCReq, ClaimCheck}
import org.michal.domain._
import org.michal.services.SerUtil

import scala.io.Codec

case object Shutdown

class UserEntityActor extends PersistentActor with ActorLogging with ClaimCheck {

  var state: Option[User] = None

  var eventsSinceLastSnapshot: Int = 0

  //TODO: move to configuration
  def snapshotAfterCount = Option(3)

  override val ccSerializer = new SerUtil

  override def receiveCommand: Receive = {
    case Command.matcher(c) =>
      log.info(s"Handling command ${c.id}")
      handleCommand(state, c) match {
        case Event.matcher(ev) =>
          log.info(s"Handling event ${ev.id}")
          persist(ev) { persEv =>
            handleEvent(persEv)
            if (snapshotAfterCount.isDefined) {
              eventsSinceLastSnapshot += 1
              maybeSnapshot()
            }
            sender ! persEv
          }
        case Notification.matcher(n) =>
          log.info(n.toString)
          sender ! n.toString
      }
      //TODO: no matcher
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
    case SaveSnapshotSuccess(metadata) =>
      log.info(s"Snapshot saving success for persistenceId: ${metadata.persistenceId}, ${metadata.sequenceNr}")
    case SaveSnapshotFailure(metadata, cause) =>
      log.error(cause, s"Save snapshot failure for persistenceId: ${metadata.persistenceId}")
    case Shutdown =>
      context.stop(self)
  }


  override def receiveRecover: Receive = userRecover

  override def persistenceId: String = URLDecoder.decode(self.path.name, Codec.UTF8.name)

  def handleCommand(u: Option[User], c: Msg[Command]): Msg[_] = c.payload match {
    case cr: CreateUserCommand => state match {
      case None => Msg(CreateUserEvent(cr.user), c)
      case Some(_) => Msg(StringNotification("User already exists..."), c)
    }
  }

  def handleEvent(e: Msg[Event]): Unit = {
    state = e.payload match {
      case cr: CreateUserEvent => Some(cr.user)
      case _ => None
    }
  }

  def maybeSnapshot(): Unit = {
    snapshotAfterCount.filter(eventsSinceLastSnapshot >= _).foreach{ i =>
      saveSnapshot(state)
      eventsSinceLastSnapshot = 0
    }
  }

  def userRecover: Receive = {
    case SnapshotOffer(metadata, offeredSnapshot: Option[User]) =>
      log.info(s"Snapshot offered for persistenceId: ${metadata.persistenceId}, seqNr: ${metadata.sequenceNr}")
      state = offeredSnapshot
    case Event.matcher(e) =>
      eventsSinceLastSnapshot += 1
      log.info(s"Recover applying message: $e")
      handleEvent(e)
  }
}

object UserEntityActor {
  def props(): Props = Props(new UserEntityActor)
}
