package org.michal

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import org.michal.actor.claimcheck.{CCReq, ClaimCheck}
import org.michal.domain.{CCResponse, GetUserRequest, GetUserResponse, User}
import org.michal.services.SerUtil


class UserEntityActor extends PersistentActor with ActorLogging with ClaimCheck {

val dummyUserList: List[User] = User("255552", "dfsf345f", "rewsda324") :: User("1234132", "dfsfwerf", "rewfgg324") :: Nil

  override val ccSerializer = new SerUtil

  override def receiveCommand: Receive = {
    case GetUserRequest.matcher(r) =>
      CCActor ! CCReq(
        ccid = None,
        messages = Some(Msg(GetUserResponse(dummyUserList), r) :: Nil),
        ccMsgNo = None,
        origId = r.id,
        entityId = r.payload.userId,
        originator = Some(sender)
      )
    case CCResponse.matcher(r) =>
      r.payload.originator ! r
  }


  override def receiveRecover = ???

  override def persistenceId = ???
}

object UserEntityActor {
  def props(): Props = Props(new UserEntityActor)
}
