package org.michal.actor

import akka.actor.FSM.Failure
import akka.actor.{ActorRef, FSM, Props}
import org.michal.actor.CCProcessor._
import org.michal.domain.{CCItem, CCReq}
import org.michal.services.DataAccessService

import concurrent.duration._
import scala.language.postfixOps

class CCProcessor(dao: DataAccessService) extends FSM[CCProcessor.State, CCProcessor.Data]{

  def unexpectedFail = Failure("Unhandled state occured")

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(req: CCReq, _) =>
      self ! InternalCCReqMsg(req)
      goto(Claiming) using DataNotClaimed(Inputs(sender(), req, None))
  }

  when(Claiming, 3 seconds) {
    transform{
      //event wraps all incoming messages
      case Event(InternalCCReqMsg(r: CCReq), stateData: DataNotClaimed) =>
        val maybeItems = dao.retrieveCCItem(r.ccid) //dao -> Future -> pipeTo self
        maybeItems match {
          case Some(ar) if ar.length == r.size =>
            stay using DataClaimed(stateData.inputs.copy(items = Some(ar.toList)))
        }
    } using {
      //evaluating current state
      case FSM.State(state, stateData: DataClaimed,  _, _, _) =>
        log.info("List is present, sending to requestor")
        stateData.originator ! stateData.inputs.items.get
        stop
      case FSM.State(state, DataNotClaimed(Inputs(o, request, None)),  _, _, _) =>
        log.info(s"Claiming data for ccid: ${request.ccid}, fetch trigger...")
        self ! InternalCCReqMsg(request)
        stay
    }
  }

  whenUnhandled{
    case e @ Event(StateTimeout, data) =>
      log.error("State timeout when in state {}", stateName)
      data.originator ! unexpectedFail
      stop

    case e @ Event(other, data) =>
      log.error("Unexpected result of {} when in state {}", other, stateName)
      data.originator ! unexpectedFail
      stop
  }


}

object CCProcessor {

  def props(dao: DataAccessService): Props = Props(new CCProcessor(dao))

  sealed trait State

  case object Idle extends State

  case object Claiming extends State

  case object ClaimSuccessful extends State

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

  case class Inputs(originator: ActorRef, request: CCReq, items: Option[List[CCItem]])

  case class DataNotClaimed(inputs: Inputs) extends InputData

  case class DataClaimed(inputs: Inputs) extends InputData

  case class LookedUpData(inputs: Inputs) extends InputData {

  }

  case class InternalCCReqMsg(req: CCReq)



}