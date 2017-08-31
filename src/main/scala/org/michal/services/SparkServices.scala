package org.michal.services

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import org.michal.actor.CCProcessor
import org.michal.domain.{CCReq, User}
import org.michal.factory.DatabaseAccess
import akka.pattern.ask
import akka.util.Timeout

import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait RestService {

  val dao: DataAccessService
  implicit val system:ActorSystem
  implicit val materializer:ActorMaterializer
  val logger = Logging(system, getClass)

  implicit val timeout = Timeout(5 seconds)

  implicit def myExceptionHandler =
    ExceptionHandler {
      case e: ArithmeticException =>
        extractUri { uri =>
          complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Data is not persisted and something went wrong"))
        }
    }

  val sparkRoutes: Route = {
    get {
      path("create" / "name" / Segment / "email" / Segment) { (name: String, email: String) =>
        complete {
          val documentId = "user::" + UUID.randomUUID().toString
          try {
            val user = User(documentId,name,email)
            val isPersisted = dao.createUser(user)
            if (isPersisted) {
              HttpResponse(StatusCodes.Created, entity = s"Data is successfully persisted with id $documentId")
            } else {
              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for id : $documentId")
            }
          } catch {
            case ex: Throwable =>
              logger.error(ex, ex.getMessage)
              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for id : $documentId")
          }
        }
      }
    } ~ path("retrieve" / "id" / Segment) { (listOfIds: String) =>
      get {
        complete {
          try {
            val idAsRDD: Option[Array[User]] = dao.retrieveUser(listOfIds)
            idAsRDD match {
              case Some(data) => HttpResponse(StatusCodes.OK, entity = data.mkString(","))
              case None => HttpResponse(StatusCodes.InternalServerError, entity = s"Data is not fetched and something went wrong")
            }
          } catch {
            case ex: Throwable =>
              logger.error(ex, ex.getMessage)
              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for ids : $listOfIds")
          }
        }
      }
    } ~ path("retrievecc" / "id" / Segment) { listOfIds =>
      get {
        val ccActor = system.actorOf(CCProcessor.props(dao))
        val future: Future[Any] = ccActor ? CCReq("1", 1)
        onComplete(future) {
          case Success(s) => complete(StatusCodes.OK, s.toString)
          case Failure(e) => complete(StatusCodes.InternalServerError, e)
        }
      }
    }
  }
}
