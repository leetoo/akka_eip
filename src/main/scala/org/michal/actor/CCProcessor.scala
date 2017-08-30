package org.michal.actor

import akka.actor.{ActorRef, FSM}
import org.michal.actor.CCProcessor._
import org.michal.domain.CCReq
import org.michal.services.DataAccessService

class CCProcessor(dao: DataAccessService) extends FSM[CCProcessor.State, CCProcessor.Data]{

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(req: CCReq, _) =>
      goto(Claiming) using DataNotClaimed(Inputs(sender(), req))
  }

}

object CCProcessor {

  sealed trait State

  case object Idle extends State

  case object Claiming extends State

  case object ClaimFailed extends State

  sealed trait Data {
    def originator: ActorRef
  }

  case object Uninitialized extends Data {
    override def originator: ActorRef = ActorRef.noSender
  }

  trait InputData extends Data {
    def inputs: Inputs
    def originator: ActorRef = inputs.originator
  }

  case class Inputs(originator: ActorRef, request: CCReq)

  case class DataNotClaimed(inputs: Inputs) extends InputData

  case class LookedUpData(inputs: Inputs) extends InputData {

  }



}