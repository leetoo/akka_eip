package org.michal.actor.claimcheck

import akka.actor.FSM.Failure
import akka.actor.{ActorRef, FSM, Props}
import akka.cluster.sharding.ShardRegion.EntityId
import org.michal.actor.claimcheck.CCProcessor._
import org.michal.domain.CCResponse
import org.michal.persistence.CCRepository
import org.michal.{Msg, Payl}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

case class CCReq(ccid: Option[String], messages: Option[List[Msg[_ <: Payl]]], ccMsgNo: Option[Int], origId: String, entityId: EntityId, originator: Option[ActorRef]) {
  def isClaim: Boolean = ccid.isDefined
}

class CCProcessor(dao: CCRepository) extends FSM[CCProcessor.State, CCProcessor.Data] {

  import akka.pattern.pipe
  import context._

  def unexpectedFail = Failure("Unhandled state occured")

  startWith(Idle, Uninitialized)

  when(Idle) {
    case Event(req: CCReq, _) =>
      if (req.isClaim) {
        self ! InternalClaimReqMsg
        goto(Claiming) using ClaimData(Inputs(sender(), req, None))
      } else {
        self ! InternalCheckReqMsg
        goto(Checking) using CheckData(Inputs(sender(), req, None))
      }
  }

  when(Checking, 3 seconds) {
    case Event(InternalCheckReqMsg, stateData: CheckData) =>
      val r = stateData.inputs.request
      log.info(s"Checking items items for reqId: ${r.origId} for entityId: ${r.entityId}")

      val originator = stateData.inputs.originator

      r.messages.foreach(msgs => msgs.groupBy(_.payload.msgType).foreach(tu => {
        val ccid = dao.create(tu._2)
        val response = CCResponse(ccid, tu._1, tu._2.size, r.origId, r.entityId, r.originator.get)
        originator ! Msg(response)
        log.info(s"Checked: ${r.origId}, ccid: $ccid, msgType: ${tu._1}, entityId: ${r.entityId}, no of messages-per-payload-type: ${tu._2.size}")
      }))

      stop
  }

  when(Claiming, 5 seconds) {

    case Event(InternalClaimReqMsg, stateData: ClaimData) =>
      def claimedItemsWrap: List[Msg[_ <: Payl]] => ClaimedItems = msg => ClaimedItems(msg)

      val originReq = stateData.inputs.request
      log.info(s"Claiming items for ccid: ${originReq.ccid.get} for entityId: ${originReq.entityId}")

      (dao.retrieveC(originReq.ccid.get).map(claimedItemsWrap) pipeTo self).recoverWith {
        case e =>
          log.error(s"Error claiming items for ccid: ${originReq.ccid.get} for entityId: ${originReq.entityId} check repository logs...")
          Future.successful(ClaimFailed) pipeTo self
      }
      stay

    case Event(ClaimFailed(), stateData: ClaimData) =>
      val originReq = stateData.inputs.request
      log.error(s"Claiming origin request details for ccid: ${originReq.ccid} => ${originReq.toString} for entityId: ${originReq.entityId}")
      stateData.originator ! Failure(originReq.ccid.get)
      stop

    case Event(ClaimedItems(list: List[Msg[Payl]]), stateData: ClaimData) =>
      val originReq = stateData.inputs.request
      val claimedPayloadType = list.headOption.fold("unspecified")(m => m.payload.msgType)

      log.info(s"Claimed ${list.size} items for ccid: ${originReq.ccid.get} of type: $claimedPayloadType for entityId: ${originReq.entityId}")

      stateData.inputs.request.ccMsgNo.fold {
        log.warning(s"Claim ccid: ${originReq.ccid.get} doesn't contain expected items info, got ${list.size}, sending items to requestor...")
        list.foreach(stateData.originator ! _)
        stop
      } {
        p =>
          if (list.size < p) {
            log.warning(s"Claim ccid: ${originReq.ccid.get} expected $p items got ${list.size}, retrying...")
            self ! InternalClaimReqMsg
            stay
          } else {
            log.info(s"Claim ccid: ${originReq.ccid.get} expected $p items got ${list.size}, sending items to requestor...")
            list.foreach(stateData.originator ! _)
            stop
          }
      }

  }

  whenUnhandled {
    case e @ Event(StateTimeout, data) =>
      log.error("State timeout when in state {}, data: {}", stateName, data)
      data.originator ! unexpectedFail
      stop

    case e @ Event(other, data) =>
      log.error("Unexpected result of {} when in state {}, data: {}", other, stateName, data)
      data.originator ! unexpectedFail
      stop
  }

}

object CCProcessor {

  def props(dao: CCRepository): Props = Props(new CCProcessor(dao))

  sealed trait State

  case object Idle extends State

  case object Claiming extends State

  case object Checking extends State

  sealed trait Data {
    def originator: ActorRef
  }

  case object Uninitialized extends Data {
    override def originator: ActorRef = ActorRef.noSender
  }

  sealed trait InputData extends Data {
    def inputs: Inputs
    def originator: ActorRef = inputs.originator
  }

  case class Inputs(originator: ActorRef, request: CCReq, items: Option[List[Msg[_ <: Payl]]])

  private case class ClaimData(inputs: Inputs) extends InputData
  case class CheckData(inputs: Inputs) extends InputData

  private case object InternalCheckReqMsg
  private case object InternalClaimReqMsg
  private case class ClaimedItems(items: List[Msg[_ <: Payl]])
  private case class ClaimFailed()

}