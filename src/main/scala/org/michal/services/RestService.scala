package org.michal.services

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import org.michal.domain.{CreateUserCommand, GetUserRequest, User}
import akka.pattern.ask
import akka.util.Timeout
import org.michal.Msg
import org.michal.actor.claimcheck.{CCProcessor, RestRequestHandler}

import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait RestService {

  val cluster: ActorRef

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
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
//    get {
      //      path("create" / "name" / Segment / "email" / Segment) { (name: String, email: String) =>
      //        complete {
      //          val documentId = "user::" + UUID.randomUUID().toString
      //          try {
      //            val user = User(documentId,name,email)
      //            val isPersisted = cluster ? GetUserRequest(user)
      //            if (isPersisted) {
      //              HttpResponse(StatusCodes.Created, entity = s"Data is successfully persisted with id $documentId")
      //            } else {
      //              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for id : $documentId")
      //            }
      //          } catch {
      //            case ex: Throwable =>
      //              logger.error(ex, ex.getMessage)
      //              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for id : $documentId")
      //          }
      //        }
      //      }
      //    } ~ path("retrieve" / "id" / Segment) { (listOfIds: String) =>
      //      get {
      //        complete {
      //          try {
      //            val idAsRDD: Option[Array[User]] = dao.retrieveUser(listOfIds)
      //            idAsRDD match {
      //              case Some(data) => HttpResponse(StatusCodes.OK, entity = data.mkString(","))
      //              case None => HttpResponse(StatusCodes.InternalServerError, entity = s"Data is not fetched and something went wrong")
      //            }
      //          } catch {
      //            case ex: Throwable =>
      //              logger.error(ex, ex.getMessage)
      //              HttpResponse(StatusCodes.InternalServerError, entity = s"Error found for ids : $listOfIds")
      //          }
      //        }
      //      }
      //    } ~
      path("createuser" / "id" / Segment) { id =>
        get {
          val future: Future[Any] = system.actorOf(RestRequestHandler.props(cluster)) ? Msg(CreateUserCommand(User(id, "lolo", "asid@wp")))
          onComplete(future) {
            case Success(s) => complete(StatusCodes.OK, s.toString)
            case Failure(e) => complete(StatusCodes.InternalServerError, e)
          }
        }
      } ~
      path("retrievecc" / "id" / Segment) { listOfIds =>
        get {
          val future: Future[Any] = system.actorOf(RestRequestHandler.props(cluster)) ? GetUserRequest("1")
          onComplete(future) {
            case Success(s) => complete(StatusCodes.OK, s.toString)
            case Failure(e) => complete(StatusCodes.InternalServerError, e)
          }
        }
      } ~
        path("hello") {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
          }
        }
//    }
  }

}
