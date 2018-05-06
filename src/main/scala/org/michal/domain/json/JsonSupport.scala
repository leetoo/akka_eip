package org.michal.domain.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.michal.domain.User
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import spray.json.DefaultJsonProtocol.jsonFormat3

trait JsonSupport extends SprayJsonSupport {
  import DefaultJsonProtocol._

  implicit val userJsonFormat: RootJsonFormat[User] = jsonFormat3(User)
}
