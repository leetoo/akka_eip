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
import org.michal.domain.json.JsonSupport

import concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}


trait RestService extends JsonSupport {

  val cluster: ActorRef

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  val logger = Logging.getLogger(system, this)

  implicit val timeout = Timeout(10 seconds)

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
      path("createuser" / "name" / Segment / "email" / Segment) { (name, email) =>
        get {
          val msgId = UUID.randomUUID().toString
          val usrId = UUID.randomUUID().toString
          logger.info(s"Create user: msgId: $msgId, userId: $usrId")
          val future: Future[Any] = system.actorOf(RestRequestHandler.props(cluster)) ?
            Msg(CreateUserCommand(User(usrId, name, email)), msgId)
          onComplete(future) {
            case Success(s) => complete(StatusCodes.OK, s.toString)
            case Failure(e) => complete(StatusCodes.InternalServerError, e)
          }
        }
      } ~
      path("getuser" / "id" / Segment) { id =>
        get {
          val msgId = UUID.randomUUID().toString
          logger.info(s"Get user: msgId: $msgId, userId: $id")
          val future = system.actorOf(RestRequestHandler.props(cluster)) ? Msg(GetUserRequest(id), msgId)
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
      } ~
      pathPrefix("createuser_v2") {
        concat(
          post {
            entity(as[User]) { user =>
              val msgId = UUID.randomUUID().toString
              val usrId = UUID.randomUUID().toString
              logger.info(s"Create user: msgId: $msgId, userId: $usrId")
              val future: Future[Any] = system.actorOf(RestRequestHandler.props(cluster)) ?
                Msg(CreateUserCommand(user), msgId)
              onComplete(future) {
                case Success(s) => complete(StatusCodes.OK, s.toString)
                case Failure(e) => complete(StatusCodes.InternalServerError, e)
              }
            }
          }
        )
      }
//    }
  }

}
